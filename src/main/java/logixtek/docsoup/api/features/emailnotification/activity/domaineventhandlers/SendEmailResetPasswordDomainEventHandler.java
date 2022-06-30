package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailResetPasswordDomainEvent;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.RequiredArgsConstructor;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component("SendEmailResetPasswordDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailResetPasswordDomainEventHandler implements Notification.Handler<SendEmailResetPasswordDomainEvent> {
    private final EmailSenderService _emailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailResetPasswordDomainEventHandler.class);

    @Value("${mail.from}")
    private String _mailFrom;

    @Value("${docsoup.client.url}")
    private String _clientUrl;

    @Override
    public void handle(SendEmailResetPasswordDomainEvent notification) {
        var classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("templates/forgotPasswordTemplate.html")) {
            var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            IOUtils.closeQuietly(is);

            if (!Strings.isNullOrEmpty(htmlString)) {
                var logoImageUrl = _clientUrl + "/img/logo-black.png";
                var emailImageUrl = _clientUrl + "/img/forgot-password-email.png";

                var subject = "Reset password instructions";
                var resetPasswordLink = _clientUrl + "/reset-password/?token=" + notification.getAccount().getToken();
                htmlString = htmlString
                        .replaceAll("@logoImage", logoImageUrl)
                        .replaceAll("@emailImage", emailImageUrl)
                        .replaceAll("@userName", notification.getAccount().getFirstName() + notification.getAccount().getLastName())
                        .replaceAll("@resetPassLink", resetPasswordLink);

                _emailSenderService.sendHtmlMessage(_mailFrom, notification.getAccount().getEmail(), subject, htmlString);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

