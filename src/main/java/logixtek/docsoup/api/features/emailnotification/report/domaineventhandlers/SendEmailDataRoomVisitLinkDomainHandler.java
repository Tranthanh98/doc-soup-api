package logixtek.docsoup.api.features.emailnotification.report.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailDataRoomVisitLinkDomainEvent;
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

@Component("SendEmailDataRoomVisitLinkDomainHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailDataRoomVisitLinkDomainHandler implements Notification.Handler<SendEmailDataRoomVisitLinkDomainEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailDataRoomVisitLinkDomainHandler.class);
    private final EmailSenderService _emailSenderService;
    @Value("${docsoup.server.url}")
    private String _serverUrl;
    @Value("${docsoup.client.url}")
    private String _clientUrl;
    @Value("${mail.from}")
    private String _mailFrom;

    @Override
    public void handle(SendEmailDataRoomVisitLinkDomainEvent notification) {
        var subject = "Export finished for " + notification.getSendEmailDataRoomVisitLinkRequestMessage().getDataRoomName();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        var htmlTemplate = "templates/visitorExportEmailTemplate.html";
        try (InputStream is = classloader.getResourceAsStream(htmlTemplate)) {
            var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            IOUtils.closeQuietly(is);

            var logoImageUrl = _clientUrl + "/img/logo-black.png";

            var downloadLink = _serverUrl + "/api/guest/download/csv?resourceId=" + notification.getSendEmailDataRoomVisitLinkRequestMessage().getBucketKey();

            htmlString = htmlString
                    .replaceAll("@logoImage", logoImageUrl)
                    .replaceAll("@userName", notification.getSendEmailDataRoomVisitLinkRequestMessage().getUserName())
                    .replaceAll("@downloadLink", downloadLink)
                    .replaceAll("@documentOrDataRoomName", notification.getSendEmailDataRoomVisitLinkRequestMessage().getDataRoomName());

            _emailSenderService.sendHtmlMessage(_mailFrom, notification.getSendEmailDataRoomVisitLinkRequestMessage().getEmailTo(), subject, htmlString);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
