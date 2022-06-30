package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailActivitiesWeeklySummaryDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.FileEntityWithVisits;
import logixtek.docsoup.api.infrastructure.models.SimplifiedDataRoomInfo;
import logixtek.docsoup.api.infrastructure.models.TeamActivityStatistic;
import logixtek.docsoup.api.infrastructure.repositories.*;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component("SendEmailActivitiesWeeklySummaryDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailActivitiesWeeklySummaryDomainEventHandler implements Notification.Handler<SendEmailActivitiesWeeklySummaryDomainEvent> {
    private final CompanyRepository companyRepository;
    private final LinkRepository linkRepository;
    private final LinkStatisticRepository linkStatisticRepository;
    private final CompanyUserRepository companyUserRepository;
    private final FileRepository fileRepository;
    private final DataRoomRepository dataRoomRepository;

    private final AccountService accountService;
    private final EmailSenderService emailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailActivitiesWeeklySummaryDomainEvent.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Override
    public void handle(SendEmailActivitiesWeeklySummaryDomainEvent notification) {
        var companyOption = companyRepository.findById(notification.getRequestMessage().getCompanyId());
        if(companyOption.isPresent()) {
            var account = accountService.get(notification.getRequestMessage().getAccountId());
            if(account != null) {
                Instant now = Instant.now();
                Instant monday = now.minus(7, ChronoUnit.DAYS);

                var classloader = Thread.currentThread().getContextClassLoader();
                try {
                    try (InputStream is = classloader.getResourceAsStream("templates/visitActivitiesWeeklySummaryTemplate.html")) {
                        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                        IOUtils.closeQuietly(is);

                        var subject = companyOption.get().getName() + " weekly activity report";

                        htmlString = htmlString
                                .replaceAll("@teamActivity", buildTeamActivityReport(notification.getRequestMessage().getCompanyId(), monday, now))
                                .replaceAll("@teammateActivity", buildTeammateActivity(notification.getRequestMessage().getCompanyId()))
                                .replaceAll("@latestFileTemplate", buildLatestContent(notification.getRequestMessage().getCompanyId(), monday.atOffset(ZoneOffset.UTC), now.atOffset(ZoneOffset.UTC)))
                                .replaceAll("@fullName", account.getFirstName() + " " + account.getLastName());

                        htmlString = buildTeamContentActivity(notification, now, monday, htmlString);

                        emailSenderService.sendHtmlMessage(mailFrom, account.getEmail(), subject, htmlString);
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private String buildTeamContentActivity(SendEmailActivitiesWeeklySummaryDomainEvent notification, Instant now, Instant monday, String htmlString) {
        var allFileWithCreatedLinkOption = fileRepository
                .findAllFileWithLinkByCompanyIdAndLinkCreatedBetweenDateOrderByLinks(notification.getRequestMessage().getCompanyId(), monday.atOffset(ZoneOffset.UTC), now.atOffset(ZoneOffset.UTC));

        if(allFileWithCreatedLinkOption.isPresent() &&
                !allFileWithCreatedLinkOption.get().isEmpty()) {
            var allFileWithCreatedLink = allFileWithCreatedLinkOption.get();
            var allLinksCreated = allFileWithCreatedLink.stream().filter(item -> item.getLinks() != null).mapToInt(FileEntityWithVisits::getLinks).sum();
            if(allLinksCreated > 0) {
                var topFileWithCreatedLink = allFileWithCreatedLink.stream().findFirst().get();
                var topCreatedLinksFilePercent = (topFileWithCreatedLink.getLinks()*100) / allLinksCreated;
                htmlString = htmlString
                        .replaceAll("@topCreatedLinksFilePercent", topCreatedLinksFilePercent +"%")
                        .replaceAll("@topCreatedLinksFileName",topFileWithCreatedLink.getDisplayName());
            } else {
                htmlString = htmlString
                        .replaceAll("@topCreatedLinksFilePercent", "")
                        .replaceAll("@topCreatedLinksFileName","");
            }
        } else {
            htmlString = htmlString
                    .replaceAll("@topCreatedLinksFilePercent", "")
                    .replaceAll("@topCreatedLinksFileName","");
        }

        var allFileWithVisitOption = fileRepository
                .findAllFileWithVisitByCompanyIdAndLinkCreatedBetweenDateOrderByVisits(notification.getRequestMessage().getCompanyId(), monday.atOffset(ZoneOffset.UTC), now.atOffset(ZoneOffset.UTC));

        if(allFileWithVisitOption.isPresent() &&
                !allFileWithVisitOption.get().isEmpty()) {
            var allFileWithVisit = allFileWithVisitOption.get();
            var allVisits = allFileWithVisit.stream().filter(item -> item.getRecentVisits() != null).mapToLong(FileEntityWithVisits::getRecentVisits).sum();
            if(allVisits > 0) {
                var topVisitFile = allFileWithVisit.stream().findFirst().get();
                var topVisitFilePercent = (topVisitFile.getRecentVisits() * 100) / allVisits;
                htmlString = htmlString
                        .replaceAll("@topVisitFilePercent", topVisitFilePercent + "%")
                        .replaceAll("@topVisitFileName",topVisitFile.getDisplayName());
            } else {
                htmlString = htmlString
                        .replaceAll("@topVisitFilePercent", "")
                        .replaceAll("@topVisitFileName", "");
            }
        } else {
            htmlString = htmlString
                    .replaceAll("@topVisitFilePercent", "")
                    .replaceAll("@topVisitFileName", "");
        }

        var allDataRoomWithCreatedLinkOption = dataRoomRepository
                .findAllDataRoomWithLinkByCompanyIdAndLinkCreatedBetweenDateOrderByLinks(notification.getRequestMessage().getCompanyId(), monday.atOffset(ZoneOffset.UTC), now.atOffset(ZoneOffset.UTC));

        if(allDataRoomWithCreatedLinkOption.isPresent() &&
                !allDataRoomWithCreatedLinkOption.get().isEmpty()) {
            var allDataRoomWithCreatedLink = allDataRoomWithCreatedLinkOption.get();
            var allLinksCreated = allDataRoomWithCreatedLink.stream().filter(item -> item.getLinks() != null).mapToInt(SimplifiedDataRoomInfo::getLinks).sum();
            if(allLinksCreated > 0) {
                var topDataRoomWithCreatedLink = allDataRoomWithCreatedLink.stream().findFirst().get();
                var topCreatedLinksDataRoomPercent = (topDataRoomWithCreatedLink.getLinks()*100) / allLinksCreated;
                htmlString = htmlString
                        .replaceAll("@topCreatedLinksDataroomPercent", topCreatedLinksDataRoomPercent +"%")
                        .replaceAll("@topCreatedLinksDataroomName",topDataRoomWithCreatedLink.getName());
            } else {
                htmlString = htmlString
                        .replaceAll("@topCreatedLinksDataroomPercent", "")
                        .replaceAll("@topCreatedLinksDataroomName","");
            }
        } else {
            htmlString = htmlString
                    .replaceAll("@topCreatedLinksDataroomPercent", "")
                    .replaceAll("@topCreatedLinksDataroomName","");
        }

        return htmlString;
    }

    private Map<Integer, Double> createTeamActivityStatistic(Optional<List<TeamActivityStatistic>> linkCreatedStatisticOption, String activityType) {
        var result = initialActivity();
        if(linkCreatedStatisticOption.isPresent() && !linkCreatedStatisticOption.get().isEmpty()) {
            var teamActivityStatistics = linkCreatedStatisticOption.get();
            int allStatistic = 0;
            if(activityType.equals(LinkConstant.CREATE_LINK_ACTIVITY)) {
                allStatistic = teamActivityStatistics.stream().filter(item -> item.getLinksCreated() != null).mapToInt(TeamActivityStatistic::getLinksCreated).sum();
            }

            if(activityType.equals(LinkConstant.VISIT_LINK_ACTIVITY)) {
                allStatistic = teamActivityStatistics.stream().filter(item -> item.getVisits() != null).mapToInt(TeamActivityStatistic::getVisits).sum();
            }

            int finalAllStatistic = allStatistic;
            teamActivityStatistics.forEach(item -> {
                if(item.getLinksCreated() != null && activityType.equals(LinkConstant.CREATE_LINK_ACTIVITY)) {
                    var percentOfDay = (Double.valueOf(item.getLinksCreated()) * 100 )/ finalAllStatistic;
                    result.replace(Utils.getDayNumberOfWeek(item.getCreatedDate()), percentOfDay);
                }

                if(item.getVisits() != null && activityType.equals(LinkConstant.VISIT_LINK_ACTIVITY)) {
                    var percentOfDay = (Double.valueOf(item.getVisits()) * 100 )/ finalAllStatistic;
                    result.replace(Utils.getDayNumberOfWeek(item.getCreatedDate()), percentOfDay);
                }
            });
        }

        return result;
    }

    private Map<Integer, Double> initialActivity() {
        Map<Integer, Double> map = new HashMap();
        map.put(Calendar.MONDAY, 0D);
        map.put(Calendar.TUESDAY, 0D);
        map.put(Calendar.WEDNESDAY, 0D);
        map.put(Calendar.THURSDAY, 0D);
        map.put(Calendar.FRIDAY, 0D);
        map.put(Calendar.SATURDAY, 0D);
        map.put(Calendar.SUNDAY, 0D);

        return map;
    }

    private String buildTeamActivityReport(UUID companyId, Instant startDate, Instant endDate) throws IOException {

        var linkCreatedStatisticOption = linkRepository
                .findAllLinkCreatedByCompanyIdAndCreatedDateBetweenGroupByCreatedDate(companyId, startDate.atOffset(ZoneOffset.UTC), endDate.atOffset(ZoneOffset.UTC));
        var createdLinkMap = createTeamActivityStatistic(linkCreatedStatisticOption, LinkConstant.CREATE_LINK_ACTIVITY);

        var linkVisitOption = linkStatisticRepository
                .findAllVisitsByCompanyIdAndViewedAtBetweenGroupByViewedAt(companyId, startDate.atOffset(ZoneOffset.UTC), endDate.atOffset(ZoneOffset.UTC));
        var visitMap = createTeamActivityStatistic(linkVisitOption, LinkConstant.VISIT_LINK_ACTIVITY);

        var rows = new ArrayList<String>();
        var classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("templates/teamActivityTemplate.html");

        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(is);

        for(int i = 1; i<=7; i++) {
            var createdLinkPercent = createdLinkMap.get(i);
            var visitPercent = visitMap.get(i);
            var row = htmlString
                    .replaceAll("@dayOfWeek", Utils.getDayStringOfWeek(i))
                    .replaceAll("@createdLinkPercent", createdLinkPercent.toString())
                    .replaceAll("@noCreatedLinkPercent", String.valueOf(100D - createdLinkPercent))
                    .replaceAll("@visitPercent", visitPercent.toString())
                    .replaceAll("@noVisitPercent", String.valueOf(100D - visitPercent));

            rows.add(row);
        }


        return String.join("\n", rows);
    }

    private String buildTeammateActivity(UUID companyId) throws IOException {
        var rows = new ArrayList<String>();
        var classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("templates/teammateActivityTemplate.html");

        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(is);
        final int SEVEN_DAY = 7;
        final int PAGE_ONE = 0;
        final int PAGE_SIZE = 10000;
        var usersWithStatisticOption = companyUserRepository
                .findTeammateWithStatisticData(SEVEN_DAY, companyId.toString(), PAGE_ONE, PAGE_SIZE);

        if(usersWithStatisticOption.isPresent() && !usersWithStatisticOption.get().isEmpty()) {
            var usersWithStatistic = usersWithStatisticOption.get();
            usersWithStatistic.forEach(user -> {
                var row = htmlString
                        .replaceAll("@firstCharacter", Utils.getFirstCharacterOfContact(user.getFullName()))
                        .replaceAll("@teammateFullName", user.getFullName())
                        .replaceAll("@teammateLinks", String.valueOf(user.getLinks()))
                        .replaceAll("@teammateDatarooms", String.valueOf(user.getDataRooms()))
                        .replaceAll("@teammateVisits", String.valueOf(user.getVisits()))
                        .replaceAll("@teammateRoomLinks", String.valueOf(user.getRoomLinks()));

                rows.add(row);
            });
        }

        return String.join("\n", rows);
    }

    private String buildLatestContent(UUID companyId, OffsetDateTime fromDate, OffsetDateTime toDate) throws IOException {
        var rows = new ArrayList<String>();
        var classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("templates/latestFileTemplate.html");

        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(is);

        var latestFilesOption = fileRepository
                .findAllByCompanyIdAndCreatedDateBetween(companyId, fromDate, toDate);

        if(latestFilesOption.isPresent() && !latestFilesOption.get().isEmpty()) {
            var latestFiles = latestFilesOption.get();
            latestFiles.forEach(file -> {
                var row = htmlString
                        .replaceAll("@fileName", file.getDisplayName())
                        .replaceAll("@firstCharacterOfCreator", Utils.getFirstCharacterOfContact(file.getOwnerName()));

                rows.add(row);
            });
        }

        return String.join("\n", rows);
    }
}
