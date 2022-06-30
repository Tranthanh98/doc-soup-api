package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import com.nimbusds.jose.shaded.json.JSONObject;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailVerifyEmailLinkDomainEvent;
import logixtek.docsoup.api.infrastructure.repositories.*;
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

@Component("SendEmailValidationEmailLinkDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailVerifyEmailLinkDomainEventHandler implements Notification.Handler<SendEmailVerifyEmailLinkDomainEvent> {
    private final EmailSenderService emailSenderService;
    private final LinkStatisticRepository linkStatisticRepository;
    private final EncryptService encryptService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailVerifyEmailLinkDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Override
    public void handle(SendEmailVerifyEmailLinkDomainEvent notification) {
        var linkStatisticOption = linkStatisticRepository.findById(notification.getRequestMessage().getLinkStatisticId());
        if(linkStatisticOption.isPresent()) {
            var linkStatistic = linkStatisticOption.get();
            var jsonObject = new JSONObject();
            jsonObject.put("linkStatisticId", linkStatistic.getId());
            jsonObject.put("linkId", linkStatistic.getLinkId().toString());
            jsonObject.put("deviceId", linkStatistic.getDeviceId());

            var jsonString = jsonObject.toJSONString();

            var classloader = Thread.currentThread().getContextClassLoader();
            try (InputStream is = classloader.getResourceAsStream("templates/verifyEmailTemplate.html")) {

                var token = encryptService.encrypt(jsonString);

                var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                IOUtils.closeQuietly(is);

                if (!Strings.isNullOrEmpty(htmlString)) {
                    var logoImageUrl = clientUrl + "/img/logo-black.png";
                    var emailImageUrl = clientUrl + "/img/group-195-2x.png";

                    var subject = "Please verify your email address to access " + notification.getRequestMessage().getLinkCreatorName() + "’s Document";
                    var verifyLink = clientUrl + "/view/" + linkStatistic.getLinkId() + "?token=" + token;
                    htmlString = htmlString
                            .replaceAll("@logoImage", logoImageUrl)
                            .replaceAll("@emailImage", emailImageUrl)
                            .replaceAll("@linkCreatorName", notification.getRequestMessage().getLinkCreatorName())
                            .replaceAll("@verifyLink", verifyLink);

                    emailSenderService.sendHtmlMessage(mailFrom, notification.getRequestMessage().getEmail(), subject, htmlString);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
