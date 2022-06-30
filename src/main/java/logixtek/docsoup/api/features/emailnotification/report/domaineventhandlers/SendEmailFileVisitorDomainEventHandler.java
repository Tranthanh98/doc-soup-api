package logixtek.docsoup.api.features.emailnotification.report.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailExportFileVisitorDomainEvent;
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

@Component("SendEmailFileVisitorDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailFileVisitorDomainEventHandler implements Notification.Handler<SendEmailExportFileVisitorDomainEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailFileVisitorDomainEventHandler.class);
    private final EmailSenderService _emailSenderService;
    @Value("${docsoup.server.url}")
    private String _serverUrl;
    @Value("${docsoup.client.url}")
    private String _clientUrl;
    @Value("${mail.from}")
    private String _mailFrom;
    @Override
    public void handle(SendEmailExportFileVisitorDomainEvent notification) {
        var subject = "Export finished for " + notification.getRequestMessage().getFileName();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        var htmlTemplate = "templates/visitorExportEmailTemplate.html";
        try (InputStream is = classloader.getResourceAsStream(htmlTemplate)) {
            var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            IOUtils.closeQuietly(is);

            var logoImageUrl = _clientUrl + "/img/logo-black.png";

            var downloadLink = _serverUrl + "/api/guest/download/csv?resourceId=" + notification.getRequestMessage().getBucketKey();

            htmlString = htmlString
                    .replaceAll("@logoImage", logoImageUrl)
                    .replaceAll("@userName", notification.getRequestMessage().getUserName())
                    .replaceAll("@downloadLink", downloadLink)
                    .replaceAll("@documentOrDataRoomName", notification.getRequestMessage().getFileName());

            _emailSenderService.sendHtmlMessage(_mailFrom, notification.getRequestMessage().getEmailTo(), subject, htmlString);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
