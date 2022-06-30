package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import com.fasterxml.jackson.databind.ObjectMapper;
import logixtek.docsoup.api.features.link.models.SecureOption;
import logixtek.docsoup.api.features.link.models.SecureValue;
import logixtek.docsoup.api.features.link.statistic.mappers.CreateLinkStatisticMapper;
import logixtek.docsoup.api.features.link.statistic.queries.GetLinkStatistic;
import logixtek.docsoup.api.features.link.view.queries.GetLink;
import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.features.link.view.services.LinkViewService;
import logixtek.docsoup.api.features.share.dtos.SendEmailDisableLinkRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Component("GetLinkHandler")
@AllArgsConstructor
public class GetLinkHandler implements Command.Handler<GetLink, ResponseMessageOf<LinkResult>> {

    private final LinkRepository linkEntityRepository;
    private  final LinkViewService linkViewService;
    private final LinkStatisticRepository linkStatisticRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;

    private final Pipeline pipeline;
    private final JobMessageQueuePublisher publisher;

    private static final Logger logger = LoggerFactory.getLogger(GetLinkHandler.class);

    @Override
    public ResponseMessageOf<LinkResult> handle(GetLink query) {
        try {
            
            if ( query.getDeviceId().isBlank() || query.getUserAgent().isBlank()) {
                return ResponseMessageOf.ofBadRequest("Invalid request", Map.of(GetLink.Fields.deviceId,
                        "x-deviceId is mandatory", GetLink.Fields.userAgent, "User-Agent is mandatory"));
            }

            var linkOption = linkEntityRepository.findById(query.getLinkId());

            if (!linkOption.isPresent() || linkOption.isEmpty() || linkOption.get().getStatus() > LinkConstant.DISABLED_STATUS) {
                return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
            }

            var link = linkOption.get();

            var creatorFullName = Strings.EMPTY;
            var creatorEmail = Strings.EMPTY;
            var creatorId = link.getCreatedBy();
            var accountOption = accountRepository.findById(link.getCreatedBy());

            if(accountOption.isPresent()) {
                creatorFullName = accountOption.get().getFirstName().concat(" ").concat(accountOption.get().getLastName());
                creatorEmail = accountOption.get().getEmail();
            }

            if(linkOption.get().getStatus() == LinkConstant.DISABLED_STATUS) {
                var jobMessage = new JobMessage<SendEmailDisableLinkRequestMessage>();

                jobMessage.setAction(JobActionConstant.SEND_EMAIL_DISABLE_LINK);
                jobMessage.setObjectName(SendEmailDisableLinkRequestMessage.class.getName());
                var body = SendEmailDisableLinkRequestMessage.of(link.getId(), creatorFullName, creatorEmail);
                jobMessage.setDataBody(body);
                publisher.sendMessage(jobMessage);

                return ResponseMessageOf.ofBadRequest("Link is disabled.",
                        Map.of(GetLink.Fields.linkId, "Link is disabled.",
                                LinkResult.Fields.creatorFullName, creatorFullName,
                                LinkResult.Fields.creatorEmail, creatorEmail));
            }

            if (link.getExpiredAt() != null && link.getExpiredAt().compareTo(OffsetDateTime.now(ZoneOffset.UTC)) < 0) {

                return ResponseMessageOf.ofBadRequest("Link was expired.",
                        Map.of(GetLink.Fields.linkId, "Link was expired",
                                LinkResult.Fields.creatorFullName, creatorFullName,
                                LinkResult.Fields.creatorEmail, creatorEmail));
            }

            LinkStatisticEntity parentViewer = null;

            if(link.getParent()!=null ) {
                var getParentViewer = GetLinkStatistic.of(query.getDeviceId(), link.getParent());
                var parentViewerResult = getParentViewer.execute(pipeline);
                if(parentViewerResult.getSucceeded() && parentViewerResult.getData().getAuthorizedAt() != null) {
                    parentViewer = parentViewerResult.getData();
                }else
                {
                    return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
                }

                if(link.getDocumentId()==null)
                {
                    return ResponseMessageOf.ofBadRequest("Invalid link.",
                            Map.of(GetLink.Fields.linkId, "Invalid link"));
                }

            }

            var isPreview = query.getAccountId() != null && Strings.isNotBlank(query.getAccountId()) && query.getAccountId().equals(creatorId);

            var companyOption = companyRepository.findById(link.getCompanyId());

            var getViewerSession = GetLinkStatistic.of(query.getDeviceId(), query.getLinkId());

            var isTrackingPreviewViewer = isPreview && companyOption.isPresent() && companyOption.get().getTrackingOwnerVisit();
            var viewerResult = getViewerSession.execute(pipeline);
            var isViewerSessionInitial = false;
            if (!viewerResult.getSucceeded()) {
                var createViewerSession = CreateLinkStatisticMapper.INSTANCE.toCommand(query);
                createViewerSession.setDocumentId(link.getDocumentId());
                if(isTrackingPreviewViewer) {
                    createViewerSession.setIsPreview(true);
                }

                if (link.getSecure() == null || link.getSecure().isBlank() || link.getSecure().isEmpty()) {
                    createViewerSession.setAuthorizedAt(Instant.now());
                }

                viewerResult = createViewerSession.execute(pipeline);
                isViewerSessionInitial = true;
                if (!viewerResult.getSucceeded()) {
                    return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            var viewerSession = viewerResult.getData();
            if (viewerSession.getAuthorizedAt() != null) {

                viewerSession.setViewedAt(OffsetDateTime.now(ZoneOffset.UTC));
                if(!isViewerSessionInitial) {
                    viewerSession.setVisit(viewerSession.getVisit()+1);
                }

                if(parentViewer!=null)
                {
                    viewerSession.setContactId(parentViewer.getContactId());
                }

                linkStatisticRepository.saveAndFlush(viewerSession);

                return buildLinkResult(link, viewerSession, creatorEmail, creatorFullName);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            var secureValue = objectMapper.readValue(link.getSecure(), SecureValue.class);

            var secureOption = new SecureOption();

            var  requirePasscode = secureValue.getPasscode()!=null && !secureValue.getPasscode().isEmpty()
                    && !secureValue.getPasscode().isBlank();

            secureOption.setPasscode(requirePasscode);
            var requireEmailAuthentication = (secureValue.getEmail()!=null && secureValue.getEmail());
            var hasAllowViewers = ((secureValue.getEmailViewers() != null && !secureValue.getEmailViewers().isEmpty()) ||
                    (secureValue.getDomainViewers() != null && !secureValue.getDomainViewers().isEmpty()));
            secureOption.setEmail(requireEmailAuthentication);
            secureOption.setHasAllowViewers(hasAllowViewers);
            secureOption.setNda(secureValue.getNda()!=null && secureValue.getNda());

            if(isTrackingPreviewViewer) {
                viewerSession.setIsPreview(true);
                linkStatisticRepository.saveAndFlush(viewerSession);
            }

            return ResponseMessageOf.of(HttpStatus.OK,
                    LinkResult.builder()
                            .ready(false)
                            .secure(secureOption)
                            .viewerId(viewerSession.getId())
                            .fileId(link.getRefId())
                            .creatorEmail(creatorEmail)
                            .creatorFullName(creatorFullName)
                            .download(link.getDownload())
                            .build());
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseMessageOf<LinkResult> buildLinkResult(LinkEntity link, LinkStatisticEntity viewerSession, String creatorEmail, String creatorFullName) {
        ResponseMessageOf<LinkResult> result;
        if(link.getDocumentId() == null) {
            result = linkViewService.fromDataRoomLink(link,viewerSession.getId());
        } else {
            result = linkViewService.fromFileLink(link,viewerSession.getId());
        }

        if(result.getSucceeded() && result.getData() != null) {
            result.getData().setDownload(link.getDownload());
            result.getData().setCreatorEmail(creatorEmail);
            result.getData().setCreatorFullName(creatorFullName);
        }

        return result;
    }
}
