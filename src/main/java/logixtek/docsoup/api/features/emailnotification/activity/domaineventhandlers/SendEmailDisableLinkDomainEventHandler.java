package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailDisableLinkDomainEvent;
import logixtek.docsoup.api.infrastructure.repositories.*;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component("SendEmailDisableLinkDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailDisableLinkDomainEventHandler implements Notification.Handler<SendEmailDisableLinkDomainEvent> {
    private final EmailSenderService emailSenderService;
    private final LinkRepository linkEntityRepository;
    private final FileRepository fileRepository;
    private final LinkAccountsRepository linkAccountsRepository;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailDisableLinkDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Override
    public void handle(SendEmailDisableLinkDomainEvent notification) {
        var linkOption = linkEntityRepository.findById(notification.getSendEmailDisableLinkRequestMessage().getLinkId());
        if(linkOption.isPresent()) {
            var link = linkOption.get();
            if(link.getDocumentId() != null) {
                var fileOption = fileRepository.findById(link.getRefId());
                var linkAccountOption = linkAccountsRepository.findById(link.getLinkAccountsId());
                if(fileOption.isPresent() && linkAccountOption.isPresent()) {
                    var file = fileOption.get();
                    var linkAccount =linkAccountOption.get();

                    var classloader = Thread.currentThread().getContextClassLoader();
                    try (InputStream is = classloader.getResourceAsStream("templates/visitDeactivationLinkTemplate.html")) {
                        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                        IOUtils.closeQuietly(is);

                        if (!Strings.isNullOrEmpty(htmlString)) {
                            var logoImageUrl = clientUrl + "/img/logo-black.png";
                            var pdfImage = clientUrl + "/img/default-pdf-thumbnail.png";
                            var linkImage = clientUrl + "/img/hyperlink-2x.png";

                            var subject = "Someone attempted to visit your disabled link " + linkAccount.getName();

                            var fileLink = clientUrl + "/content/file/" + file.getId();
                            htmlString = htmlString
                                    .replaceAll("@logoImage", logoImageUrl)
                                    .replaceAll("@pdfImage", pdfImage)
                                    .replaceAll("@hyperLinkUrl", linkImage)
                                    .replaceAll("@ownerName", notification.getSendEmailDisableLinkRequestMessage().getCreatorFullName())
                                    .replaceAll("@reactiveLink", fileLink)
                                    .replaceAll("@linkAccountName", linkAccount.getName())
                                    .replaceAll("@fileName", file.getName())
                                    .replaceAll("@fileLink", fileLink);

                            emailSenderService.sendHtmlMessage(mailFrom, notification.getSendEmailDisableLinkRequestMessage().getCreatorEmail(), subject, htmlString);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
