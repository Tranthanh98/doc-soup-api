package logixtek.docsoup.api.features.emailnotification.sample.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailVisitedDocumentSampleDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
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

@Component("SendEmailVisitedDocumentSampleDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailVisitedDocumentSampleDomainEventHandler implements Notification.Handler<SendEmailVisitedDocumentSampleDomainEvent> {
    private final EmailSenderService emailSenderService;
    private final AccountService accountService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailVisitedDocumentSampleDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Value("${docsoup.server.url}")
    private String serverUrl;

    @Override
    public void handle(SendEmailVisitedDocumentSampleDomainEvent notification) {
        var account = accountService.get(notification.getRequestMessage().getAccountId());
        if(account != null) {
            var classloader = Thread.currentThread().getContextClassLoader();
            try (InputStream is = classloader.getResourceAsStream("templates/visitDocumentSampleEmailTemplate.html")) {
                var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                IOUtils.closeQuietly(is);

                if (!Strings.isNullOrEmpty(htmlString)) {
                    var logoImageUrl = clientUrl + "/img/logo-black.png";
                    var pdfImage = clientUrl + "/img/default-pdf-thumbnail.png";
                    var linkImage = clientUrl + "/img/hyperlink-2x.png";
                    var circleChartUrl = serverUrl + "/api/guest/view/view-circle-chart-by-percent?percent=50.0";

                    var subject = "Sample Visit Notification: Jane Doe just visited your document sample PDF";
                    htmlString = htmlString
                            .replaceAll("@logoImage", logoImageUrl)
                            .replaceAll("@pdfImage", pdfImage)
                            .replaceAll("@hyperLinkUrl", linkImage)
                            .replaceAll("@circleChartUrl", circleChartUrl)
                            .replaceAll("@userName", account.getFirstName() + account.getLastName());

                    emailSenderService.sendHtmlMessage(mailFrom, account.getEmail(), subject, htmlString);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
