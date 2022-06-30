package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailDocumentVisitDomainEvent;
import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import logixtek.docsoup.api.infrastructure.repositories.*;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Component("SendEmailDocumentVisitDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailDocumentVisitDomainEventHandler implements Notification.Handler<SendEmailDocumentVisitDomainEvent> {
    private final EmailSenderService emailSenderService;
    private final LinkStatisticRepository linkStatisticRepository;
    private final ContactRepository contactRepository;
    private final LinkAccountsRepository linkAccountsRepository;
    private final LinkRepository linkEntityRepository;
    private final FileRepository fileRepository;
    private final PageStatisticRepository pageStatisticRepository;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailDocumentVisitDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Value("${docsoup.server.url}")
    private String serverUrl;

    @Override
    public void handle(SendEmailDocumentVisitDomainEvent notification) {
        var linkStatisticOption = linkStatisticRepository.findById(notification.getRequestMessage().getLinkStatisticId());
        if(linkStatisticOption.isPresent()) {
            var linkStatistic = linkStatisticOption.get();
            String osNameOrEmail = "";
            String[] deviceInfomations = {};
            if(!Strings.isNullOrEmpty(linkStatistic.getDeviceName())) {
                deviceInfomations = linkStatistic.getDeviceName().split("-");
            }

            ContactEntity contact = null;
            if(linkStatistic.getContactId() != null) {
                var contactOption = contactRepository.findById(linkStatistic.getContactId());
                if(contactOption.isPresent()) {
                    contact = contactOption.get();
                    osNameOrEmail = contact.getName();
                }
            } else if(deviceInfomations.length > 0) {
                osNameOrEmail = deviceInfomations[0] + " visitor";
            }

            var linkOption = linkEntityRepository.findById(linkStatistic.getLinkId());
            if(linkOption.isPresent()) {
                var link = linkOption.get();
                var fileOption = fileRepository.findById(link.getRefId());
                var linkAccountOption = linkAccountsRepository.findById(link.getLinkAccountsId());

                if(fileOption.isPresent() && linkAccountOption.isPresent()) {
                    var file = fileOption.get();
                    var linkAccount = linkAccountOption.get();

                    var classloader = Thread.currentThread().getContextClassLoader();
                    try (InputStream is = classloader.getResourceAsStream("templates/visitDocumentEmailTemplate.html")) {

                        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                        IOUtils.closeQuietly(is);

                        if (!Strings.isNullOrEmpty(htmlString)) {
                            var logoImageUrl = clientUrl + "/img/logo-black.png";
                            var pdfImage = clientUrl + "/img/default-pdf-thumbnail.png";
                            var linkImage = clientUrl + "/img/hyperlink-2x.png";

                            var subject = contact != null ? contact.getEmail() : "Someone " + " just visited your document " + file.getName();

                            var viewAllStatsLink = "";
                            if(contact != null) {
                                viewAllStatsLink = clientUrl + "/contacts/" + contact.getId() + "?selectedFileId="+file.getId();
                            } else {
                                viewAllStatsLink = clientUrl + "/content/file/" + file.getId();
                            }

                            String locationWithDevice = getLocationWithDeviceString(linkStatistic);

                            var duration = Utils.covertMillisecondToMinutesFormat(linkStatistic.getDuration());

                            Collection<PageStatistic> pageStatistics = Collections.emptyList();
                            var pageStatisticsOption = pageStatisticRepository.findAllPageStatisticByLinkStatisticId(linkStatistic.getId());
                            if(pageStatisticsOption.isPresent()) {
                                pageStatistics = pageStatisticsOption.get();
                            }

//                            var documentActivity = buildDocumentActivity(pageStatistics, link.getId());

                            var firstCharacterOfContact =Utils.getFirstCharacterOfContact(osNameOrEmail);

                            var circleChartUrl = serverUrl + "/api/guest/view/view-circle-chart-by-percent?percent=" + getViewRate(linkStatistic);
                            htmlString = htmlString
                                    .replaceAll("@logoImage", logoImageUrl)
                                    .replaceAll("@pdfImage", pdfImage)
                                    .replaceAll("@hyperLinkUrl", linkImage)
                                    .replaceAll("@contactNameCharacter", firstCharacterOfContact)
                                    .replaceAll("@contactName", osNameOrEmail)
                                    .replaceAll("@contactEmail", contact != null ? contact.getEmail() : linkAccount.getName())
                                    .replaceAll("@linkCreatorName", notification.getRequestMessage().getLinkCreatorName())
                                    .replaceAll("@viewAllStatsLink", viewAllStatsLink)
                                    .replaceAll("@circleChartUrl", circleChartUrl)
                                    .replaceAll("@duration", duration)
                                    .replaceAll("@fileName", file.getName())
                                    .replaceAll("@accountName", linkAccount.getName())
//                                    .replaceAll("@documentActivity", documentActivity)
                                    .replaceAll("@locationWithDevice", locationWithDevice);

                            emailSenderService.sendHtmlMessage(mailFrom, notification.getRequestMessage().getCreatorEmail(), subject, htmlString);

                            linkStatistic.setSentInformationEmail(true);
                            linkStatisticRepository.saveAndFlush(linkStatistic);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private Float getViewRate(LinkStatisticEntity linkStatistic) {
        var viewersOption = contactRepository.findAllViewerByLinkId(linkStatistic.getLinkId());

        Float viewRate = 0.0F;
        if(viewersOption.isPresent() && !viewersOption.get().isEmpty()) {
            var viewerOption = viewersOption.get().stream().filter(x -> x.getViewerId().equals(linkStatistic.getId())).findFirst();
            if(viewerOption.isPresent()) {
                viewRate = viewerOption.get().getViewedRate();
            }
        }

        return viewRate;
    }


    private String getLocationWithDeviceString(LinkStatisticEntity linkStatistic) {
        var device = linkStatistic.getDeviceName();
        String[] deviceInformation = {};
        if(!Strings.isNullOrEmpty(linkStatistic.getDeviceName())) {
            deviceInformation = linkStatistic.getDeviceName().split(" - ");
        }

        if (deviceInformation.length > 1) {
            device = deviceInformation[deviceInformation.length -1] + " - " + deviceInformation[deviceInformation.length -2];
        }

        return !Strings.isNullOrEmpty(linkStatistic.getLocation()) ?
                linkStatistic.getLocation() + " - " + device : device;
    }

    private String buildDocumentActivity(Collection<PageStatistic> pageStatistics, UUID linkId) {
        var rows = new ArrayList<String>();
        pageStatistics.forEach(pageStatistic -> {
            if(pageStatistic.getDuration() > 0) {
                var viewLinkUrl = clientUrl + "/view/" + linkId.toString();
                var row = "        <div class=\"container__body__document-link-row\">\n" +
                        "          <a href=\""+ viewLinkUrl + "\"" + " class=\"container__body__link\">" + viewLinkUrl + "</a>\n" +
                        "          <span class=\"container__body__page-number\">Page "+ pageStatistic.getPage() +"</span>      \n" +
                        "        </div>";
                rows.add(row);
            }
        });

        return String.join("\n", rows);
    }
}
