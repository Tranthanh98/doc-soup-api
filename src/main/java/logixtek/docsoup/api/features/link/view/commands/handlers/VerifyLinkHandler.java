package logixtek.docsoup.api.features.link.view.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.link.models.SecureValue;
import logixtek.docsoup.api.features.link.view.commands.CreateVisitorHistory;
import logixtek.docsoup.api.features.link.view.commands.VerifyLink;
import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.features.link.view.services.LinkViewService;
import logixtek.docsoup.api.features.share.domainevents.CreateContactDomainEvent;
import logixtek.docsoup.api.features.share.dtos.SendEmailDisableLinkRequestMessage;
import logixtek.docsoup.api.features.share.dtos.SendEmailVerifyEmailLinkRequestMessage;
import logixtek.docsoup.api.features.share.dtos.SendEmailDocumentVisitRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import logixtek.docsoup.api.infrastructure.entities.DeniedVisitEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.enums.ViewerAction;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.repositories.DeniedVisitRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Component("VerifyLinkHandler")
@AllArgsConstructor
public class VerifyLinkHandler implements Command.Handler<VerifyLink, ResponseMessageOf<LinkResult>> {

    private final LinkStatisticRepository repository;
    private final LinkRepository linkRepository;
    private final ContactRepository contactRepository;
    private final DeniedVisitRepository deniedVisitRepository;

    private final Pipeline pipeline;
    private final LinkViewService linkViewService;
    private final AccountService accountService;
    private final JobMessageQueuePublisher publisher;

    private static final Logger logger = LoggerFactory.getLogger(VerifyLinkHandler.class);

    @Override
    public ResponseMessageOf<LinkResult> handle(VerifyLink command) {

        try {
            if (command.getDeviceId() == null || command.getDeviceId().isEmpty()) {
                return ResponseMessageOf.ofBadRequest("Invalid request",
                        Map.of(VerifyLink.Fields.deviceId, "DeviceId is mandatory"));
            }

            if (command.getViewerId() == null || command.getViewerId() < 1) {
                return ResponseMessageOf.ofBadRequest("Invalid request",
                        Map.of(VerifyLink.Fields.viewerId, "ViewerId is mandatory"));
            }

            var viewerSessionOption = repository.findById(command.getViewerId());

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

            ObjectMapper objectMapper = new ObjectMapper();
            var secureValue = objectMapper.readValue(link.getSecure(), SecureValue.class);

            if(secureValue.getPasscode()!=null &&  !secureValue.getPasscode().equals(command.getPasscode()))
            {
                return  ResponseMessageOf.ofBadRequest("Failed",
                    Map.of(VerifyLink.Fields.passcode, "Invalid passcode."));
            }

            if(secureValue.getNda() != null && secureValue.getNda()){
                if(!command.getName().matches("^[aA-zZ\\s\\d]+$")){
                    return  ResponseMessageOf.ofBadRequest("You can only use English only",
                            Map.of(VerifyLink.Fields.name, "You can only use English only"));
                }
            }

            ContactEntity contact = null;
            if( !Strings.isNullOrEmpty(command.getEmail()) &&
                    ((secureValue.getEmail()!=null && secureValue.getEmail()) ||
                    (secureValue.getDomainViewers() != null && !secureValue.getDomainViewers().isEmpty()) ||
                    (secureValue.getEmailViewers() != null && !secureValue.getEmailViewers().isEmpty()))) {
                contact = createContact(command, link);
                viewerSession.setContactId(contact.getId());

                viewerSession = repository.saveAndFlush(viewerSession);
            }

            var linkCreator = accountService.get(link.getCreatedBy());
            if(linkCreator == null) {
                return  ResponseMessageOf.ofBadRequest(ResponseResource.NotFoundAccount, Map.of());
            }

            if(!Strings.isNullOrEmpty(command.getEmail()) &&
                    ((secureValue.getDomainViewers() != null && !secureValue.getDomainViewers().isEmpty()) ||
                            (secureValue.getEmailViewers() != null && !secureValue.getEmailViewers().isEmpty()))) {
                var emailDomain = command.getEmail().split("@")[1];

                var domainViewer = secureValue.getDomainViewers();

                var isValidByEmail = false;

                if(domainViewer!=null && domainViewer.stream().count() >0)
                {
                    isValidByEmail = domainViewer.stream().anyMatch(x->x.equals(emailDomain));
                }

                if(!isValidByEmail) {
                    var emailViewer = secureValue.getEmailViewers();

                    if (emailViewer != null && emailViewer.stream().count() > 0) {
                        isValidByEmail = emailViewer.stream().anyMatch(x->x.equals(command.getEmail()));
                    }
                }

                if(isValidByEmail) {
                    viewerSession.setFromAllowViewersLink(true);
                    if(secureValue.getEmail() == null || !secureValue.getEmail()) {
                        viewerSession.setAuthorizedAt(Instant.now());
                    }

                    repository.saveAndFlush(viewerSession);
                } else {
                    // Create denied visit history
                    var deniedVisit = new DeniedVisitEntity();
                    deniedVisit.setLinkId(link.getId());
                    deniedVisit.setEmail(command.getEmail());
                    deniedVisit.setVisitTime(OffsetDateTime.now(ZoneOffset.UTC));
                    deniedVisitRepository.saveAndFlush(deniedVisit);

                    return ResponseMessageOf.ofBadRequest("Failed",
                            Map.of(VerifyLink.Fields.email, "You do not have permission to view this document."));
                }
            }

            if(secureValue.getEmail()!=null && secureValue.getEmail() && (secureValue.getNda() == null ||  !secureValue.getNda()))
            {

                if(command.getEmail() == null || command.getEmail().isEmpty() || command.getEmail().isBlank())
                {
                    return  ResponseMessageOf.ofBadRequest("Failed",
                            Map.of(VerifyLink.Fields.email, "Wrong email"));
                }

                var jobMessage = new JobMessage<SendEmailVerifyEmailLinkRequestMessage>();

                jobMessage.setAction(JobActionConstant.SEND_EMAIL_VALIDATION_EMAIL_LINK);
                jobMessage.setObjectName(SendEmailDisableLinkRequestMessage.class.getName());
                var body = SendEmailVerifyEmailLinkRequestMessage
                        .of(viewerSession.getId(), command.getEmail(), linkCreator.getFirstName() + " " + linkCreator.getLastName());
                jobMessage.setDataBody(body);
                publisher.sendMessage(jobMessage);

                return  ResponseMessageOf.of(HttpStatus.ACCEPTED,LinkResult.builder()
                        .visitorEmail(command.getEmail())
                        .creatorFullName(linkCreator.getFirstName() + " " + linkCreator.getLastName())
                        .ready(false)
                        .viewerId(viewerSession.getId())
                        .requireVerifyEmail(true)
                        .build());
            }

            if(secureValue.getNda()!=null &&  secureValue.getNda())
            {
                viewerSession.setNDAToken(UUID.randomUUID().toString());

                if(contact != null){
                    createHistoryVisitor(command, viewerSession, contact);
                }
            } else {
                if (viewerSession.getAuthorizedAt() != null) {
                    viewerSession.setVisit((viewerSession.getVisit() +1 ));
                }

                if((secureValue.getEmailViewers() == null || secureValue.getEmailViewers().isEmpty()) &&
                        (secureValue.getDomainViewers() == null || secureValue.getDomainViewers().isEmpty()) &&
                        (secureValue.getEmail() == null || !secureValue.getEmail())) {
                    viewerSession.setAuthorizedAt(Instant.now());
                }
            }

            repository.saveAndFlush(viewerSession);

            if(viewerSession.getAuthorizedAt()!=null)
            {
                if(link.getDocumentId() == null)
                {
                    return  linkViewService.fromDataRoomLink(link,viewerSession.getId());
                }

                return linkViewService.fromFileLink(link,viewerSession.getId());

            }else
            {
                return  ResponseMessageOf.of(HttpStatus.ACCEPTED,LinkResult.builder()
                        .ready(false)
                        .downloadToken(viewerSession.getNDAToken())
                        .build());
            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private ContactEntity createContact(VerifyLink command, LinkEntity link) {
        ContactEntity result = null;
        var contactName = !Strings.isNullOrEmpty(command.getName()) ? command.getName() : command.getEmail();

        var createContactDomainEventHandler =
                CreateContactDomainEvent.of(command.getEmail(), contactName, link.getCreatedBy(), link.getCompanyId());

        createContactDomainEventHandler.send(pipeline);

        var contactOption = contactRepository.findFirstByEmailAndCompanyId(command.getEmail(), link.getCompanyId());
        if(contactOption.isPresent())
        {
            result = contactOption.get();
        }
        return result;
    }

    private Long createHistoryVisitor(VerifyLink query, LinkStatisticEntity viewer, ContactEntity contact){
        var deviceName = viewer.getDeviceName().split("-");
        var browserName = deviceName[2];

        var command = CreateVisitorHistory.builder()
                .actionType(ViewerAction.VERIFY)
                .email(contact.getEmail())
                .ipAddress(query.getIp())
                .linkId(query.getLinkId())
                .location(viewer.getLocation())
                .name(contact.getName())
                .viewerId(viewer.getId())
                .userAgent(viewer.getDeviceAgent())
                .browserName(browserName)
                .build();

        var result = command.execute(pipeline);
        return result.getBody();
    }

}
