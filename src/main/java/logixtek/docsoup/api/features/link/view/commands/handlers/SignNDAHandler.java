package logixtek.docsoup.api.features.link.view.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.view.commands.CreateVisitorHistory;
import logixtek.docsoup.api.features.link.view.commands.SignNDA;
import logixtek.docsoup.api.features.link.view.commands.VerifyLink;
import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.features.share.domainevents.CreateCertificateAndSignedDomainEvent;
import logixtek.docsoup.api.features.share.dtos.SendEmailDisableLinkRequestMessage;
import logixtek.docsoup.api.features.share.dtos.SendEmailVerifyEmailLinkRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.enums.ViewerAction;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component("SignedNDAHandler")
@AllArgsConstructor
public class SignNDAHandler implements Command.Handler<SignNDA, ResponseMessageOf<LinkResult>> {
    private final LinkStatisticRepository linkStatisticRepository;
    private final LinkRepository linkRepository;
    private final ContactRepository contactRepository;
    private final AccountService accountService;
    private final JobMessageQueuePublisher publisher;

    private final Pipeline pipeline;

    private final Logger logger = LoggerFactory.getLogger(SignNDAHandler.class);
    @Override
    public ResponseMessageOf<LinkResult> handle(SignNDA command) {
        if (command.getDeviceId() == null || command.getDeviceId().isEmpty()) {
            return ResponseMessageOf.ofBadRequest("Invalid request",
                    Map.of(VerifyLink.Fields.deviceId, "DeviceId is mandatory"));
        }

        if (command.getViewerId() == null || command.getViewerId() < 1) {
            return ResponseMessageOf.ofBadRequest("Invalid request",
                    Map.of(VerifyLink.Fields.viewerId, "ViewerId is mandatory"));
        }

        var viewerSessionOption = linkStatisticRepository.findById(command.getViewerId());

        if (!viewerSessionOption.isPresent()) {
            return ResponseMessageOf.ofBadRequest("Not found viewer",
                    Map.of(VerifyLink.Fields.viewerId, "Not found viewer"));
        }

        var viewerSession = viewerSessionOption.get();

        if (!viewerSession.getDeviceId().equals(command.getDeviceId())) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        if (!viewerSession.getLinkId().toString().equals(command.getLinkId().toString())) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var linkOption = linkRepository.findById(viewerSession.getLinkId());

        if (!linkOption.isPresent() || linkOption.get().getStatus() > LinkConstant.ACTIVE_STATUS) {
            return ResponseMessageOf.ofBadRequest("Link not found",
                    Map.of(VerifyLink.Fields.viewerId, "Link not found"));
        }

        var link = linkOption.get();

        var contactOption = contactRepository.findById(viewerSession.getContactId());

        if(!contactOption.isPresent()){
            return ResponseMessageOf.ofBadRequest("Contact not found", Map.of());
        }

        var contact = contactOption.get();

        createHistoryVisitor(command, ViewerAction.AGREED_TERM, viewerSession, contact);

        var creatorLink = accountService.get(link.getCreatedBy());
        if(creatorLink == null) {
            return  ResponseMessageOf.ofBadRequest(ResponseResource.NotFoundAccount, Map.of());
        }

        if(Boolean.TRUE.equals(command.getSignedNDA())) {
            viewerSession.setSignedNDA(command.getSignedNDA());
            viewerSession.setNdaId(link.getNdaId());
            viewerSession.setContactId(contact.getId());
            createHistoryVisitor(command, ViewerAction.AUTHORIZED_TO_READING, viewerSession, contact);

            linkStatisticRepository.saveAndFlush(viewerSession);

            createHistoryVisitor(command, ViewerAction.SIGNED_DOCUMENT, viewerSession, contact);

            createHistoryVisitor(command, ViewerAction.GENERATED_SIGNED, viewerSession, contact);

            raiseDomainEventCreateCertificateAndSignedNda(link.getId(), viewerSession.getId(), contact.getId());

            var jobMessage = new JobMessage<SendEmailVerifyEmailLinkRequestMessage>();

            jobMessage.setAction(JobActionConstant.SEND_EMAIL_VALIDATION_EMAIL_LINK);
            jobMessage.setObjectName(SendEmailDisableLinkRequestMessage.class.getName());
            var body = SendEmailVerifyEmailLinkRequestMessage
                    .of(viewerSession.getId(), contact.getEmail(), creatorLink.getFirstName() + " " + creatorLink.getLastName());
            jobMessage.setDataBody(body);
            try {
                publisher.sendMessage(jobMessage);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
            }
        }

        return  ResponseMessageOf.of(HttpStatus.ACCEPTED,LinkResult.builder()
                .visitorEmail(contact.getEmail())
                .creatorFullName(creatorLink.getFirstName() + " " + creatorLink.getLastName())
                .ready(false)
                .requireVerifyEmail(true)
                .build());
    }

    private Long createHistoryVisitor(SignNDA commandSignNda, ViewerAction action, LinkStatisticEntity viewer, ContactEntity contact){
        var deviceName = viewer.getDeviceName().split("-");
        var browserName = deviceName[2];

        var command = CreateVisitorHistory.builder()
                .actionType(action)
                .email(contact.getEmail())
                .ipAddress(commandSignNda.getIp())
                .linkId(commandSignNda.getLinkId())
                .location(viewer.getLocation())
                .name(contact.getName())
                .viewerId(viewer.getId())
                .userAgent(viewer.getDeviceAgent())
                .browserName(browserName)
                .build();

        var result = command.execute(pipeline);
        return result.getBody();
    }

    private void raiseDomainEventCreateCertificateAndSignedNda(UUID linkId, Long viewerId, Long contactId){
        var domainEvent = new CreateCertificateAndSignedDomainEvent(linkId, viewerId, contactId);

        domainEvent.send(pipeline);
    }
}
