package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailActivitiesDailySummaryDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.SimplifiedViewer;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component("SendEmailActivitiesDailySummaryDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailActivitiesDailySummaryDomainEventHandler implements Notification.Handler<SendEmailActivitiesDailySummaryDomainEvent> {
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    private final AccountService accountService;
    private final EmailSenderService emailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailActivitiesDailySummaryDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Override
    public void handle(SendEmailActivitiesDailySummaryDomainEvent notification) {
        var companyOption = companyRepository.findById(notification.getRequestMessage().getCompanyId());
        if(companyOption.isPresent()) {
                var account = accountService.get(notification.getRequestMessage().getAccountId());
                if(account != null) {
                    Instant now = Instant.now();
                    Instant yesterday = now.minus(1, ChronoUnit.DAYS);
                    var listViewerOption = contactRepository
                            .findAllViewerVisitDocumentByCompanyIdAndAccountIdBetweenDate(companyOption.get().getId(), account.getId(), yesterday.atOffset(ZoneOffset.UTC), now.atOffset(ZoneOffset.UTC));

                    if(listViewerOption.isPresent() && !listViewerOption.get().isEmpty()) {
                        var lisViewer = listViewerOption.get();
                        var averageCompletion = lisViewer.stream().map(x -> x.getViewedRate()).mapToDouble(Float::doubleValue).sum() / lisViewer.size();

                        var classloader = Thread.currentThread().getContextClassLoader();
                        try (InputStream is = classloader.getResourceAsStream("templates/visitActivitiesDailySummaryTemplate.html")) {

                            var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                            IOUtils.closeQuietly(is);

                            if (!Strings.isNullOrEmpty(htmlString)) {
                                var logoImageUrl = clientUrl + "/img/logo-black.png";
                                var teamUrl = clientUrl + "/settings";

                                var subject = companyOption.get().getName() + " daily activity report";
                                htmlString = htmlString
                                        .replaceAll("@logoImage", logoImageUrl)
                                        .replaceAll("@companyName", companyOption.get().getName())
                                        .replaceAll("@ownerFullName", account.getFirstName() + " " + account.getLastName())
                                        .replaceAll("@numberOfVisit", listViewerOption.get().size() + "")
                                        .replaceAll("@averageCompletion", String.format("%.2f", averageCompletion))
                                        .replaceAll("@viewerListString", buildViewerListString(lisViewer))
                                        .replaceAll("@teamUrl", teamUrl);

                                emailSenderService.sendHtmlMessage(mailFrom, account.getEmail(), subject, htmlString);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                }
            }
        }
    }

    private String buildViewerListString(Collection<SimplifiedViewer> viewerList) throws IOException {
        var rows = new ArrayList<String>();
        var classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("templates/viewerListTemplate.html")) {

            var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            IOUtils.closeQuietly(is);

            viewerList.forEach(viewer -> {
                String osNameOrEmail = "";
                String[] deviceInfomations = {};
                if(!Strings.isNullOrEmpty(viewer.getDevice())) {
                    deviceInfomations = viewer.getDevice().split("-");
                }

                if(!Strings.isNullOrEmpty(viewer.getContactName())) {
                    osNameOrEmail = viewer.getContactName();
                } else if(deviceInfomations.length > 0) {
                    osNameOrEmail = deviceInfomations[0] + " visitor";
                }

                var row = htmlString
                        .replaceAll("@firstCharacter", Utils.getFirstCharacterOfContact(osNameOrEmail))
                        .replaceAll("@viewerName", osNameOrEmail)
                        .replaceAll("@fileName", viewer.getFileName())
                        .replaceAll("@viewRate", String.format("%.2f", viewer.getViewedRate()))
                        .replaceAll("@duration", Utils.covertMillisecondToMinutesFormat(viewer.getDuration()));

                rows.add(row);
            });
        }

        return rows.stream().collect(Collectors.joining("\n"));
    }
}
