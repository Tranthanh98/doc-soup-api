package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import com.nimbusds.jose.shaded.json.JSONObject;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailDeniedVisitDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.entities.DeniedVisitEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.repositories.DeniedVisitRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

@Component("SendEmailDeniedVisitDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailDeniedVisitDomainEventHandler implements Notification.Handler<SendEmailDeniedVisitDomainEvent> {
    private final EmailSenderService emailSenderService;
    private final LinkRepository linkEntityRepository;
    private final FileRepository fileRepository;
    private final LinkAccountsRepository linkAccountsRepository;
    private final DeniedVisitRepository deniedVisitRepository;
    private final EncryptService encryptService;
    private final AccountService accountService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailDisableLinkDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Override
    public void handle(SendEmailDeniedVisitDomainEvent notification) {
        var linkOption = linkEntityRepository.findById(notification.getRequestMessage().getLinkId());
        if(linkOption.isPresent()) {
            var link = linkOption.get();
            var linkCreator = accountService.get(link.getCreatedBy());
            var fileOption = fileRepository.findById(link.getRefId());
            var linkAccountOption = linkAccountsRepository.findById(link.getLinkAccountsId());
            if(fileOption.isPresent() && linkAccountOption.isPresent() && linkCreator != null) {
                var file = fileOption.get();
                var linkAccount = linkAccountOption.get();
                var deniedVisitsOption = deniedVisitRepository.findAllByLinkIdAndSentEmailIsFalse(link.getId());
                if(deniedVisitsOption.isPresent() && !deniedVisitsOption.get().isEmpty()) {
                    var classloader = Thread.currentThread().getContextClassLoader();
                    try (InputStream is = classloader.getResourceAsStream("templates/unauthorizedVisitorEmailTemplate.html")) {
                        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                        IOUtils.closeQuietly(is);


                        if (!Strings.isNullOrEmpty(htmlString)) {
                            var logoImageUrl = clientUrl + "/img/logo-black.png";
                            var linkAccountUrl = clientUrl + "/link-account/" + linkAccount.getId();
                            var fileDetailUrl = clientUrl + "/content/file/" + file.getId();

                            var deniedVisits = deniedVisitsOption.get();

                            var allowViewerToken = generateAllowViewerToken(link, deniedVisits.stream().findFirst().get());

                            var allowEmailUrl = clientUrl + "/content/file/" + file.getId() + "?linkedLinkId=" + link.getId() +"&allowViewerToken=" + allowViewerToken;

                            var deniedVisitHistory = createDeniedVisitHistory(deniedVisits);

                            deniedVisits.forEach(item -> {
                                item.setSentEmail(true);
                            });

                            var subject = "Unauthorized viewer detected";

                            htmlString = htmlString
                                    .replaceAll("@logoImage", logoImageUrl)
                                    .replaceAll("@documentName", file.getName())
                                    .replaceAll("@linkAccountName", linkAccount.getName())
                                    .replaceAll("@linkAccountUrl", linkAccountUrl)
                                    .replaceAll("@allowEmailUrl", allowEmailUrl)
                                    .replaceAll("@deniedVisitHistory", deniedVisitHistory)
                                    .replaceAll("@allowEmailUrl", allowEmailUrl)
                                    .replaceAll("@fileDetailUrl", fileDetailUrl);

                            emailSenderService.sendHtmlMessage(mailFrom, linkCreator.getEmail(), subject, htmlString);

                            deniedVisitRepository.saveAllAndFlush(deniedVisits);
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    private String generateAllowViewerToken(LinkEntity link, DeniedVisitEntity deniedVisit) {
        var jsonObject = new JSONObject();
        jsonObject.put("linkId", link.getId().toString());
        jsonObject.put("email", deniedVisit.getEmail());

        var jsonString = jsonObject.toJSONString();

        return encryptService.encrypt(jsonString);
    }

    private String createDeniedVisitHistory(Collection<DeniedVisitEntity> deniedVisitEntities) {
        var rows = new ArrayList<String>();
        var closeImage = clientUrl + "/img/ico-cancel.png";
        deniedVisitEntities.forEach(item -> {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            var date = item.getVisitTime().format(dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            var time = item.getVisitTime().format(timeFormatter);
            var row = "        <div class=\"container__body__row\">\n" +
                    "          <img class=\"container__body__cancel-icon\" src=\""+closeImage+"\" />\n" +
                    "          <span class=\"container__body__email\">" + item.getEmail() +"</span>\n" +
                    "          <span>denied on "+ date + " at "+ time +"</span>\n" +
                    "        </div>\n";

            rows.add(row);
        });

        var divider = " <div class=\"container__body__divider\"></div>\n";
        return String.join(divider, rows);
    }
}
