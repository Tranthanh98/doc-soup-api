package logixtek.docsoup.api.features.share.schedulingtasks;

import logixtek.docsoup.api.features.share.dtos.*;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.*;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component("SendEmailScheduledTask")
@AllArgsConstructor
public class SendEmailScheduledTask {
    private final LinkStatisticRepository linkStatisticRepository;
    private final DeniedVisitRepository deniedVisitRepository;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;

    JobMessageQueuePublisher publisher;

    private final int FIVE_MINUTES = 300000;
    private final int ONE_DAY = 1;
    private final int TWO_DAYS = 2;
    private final int EIGHT_DAYS = 8;
    private static final Logger logger = LoggerFactory.getLogger(SendEmailScheduledTask.class);

    @Scheduled(fixedRate = FIVE_MINUTES)
    public void sendEmailDocumentActivity() {
        sendDeniedVisitHistoryEmail();
        sendDocumentVisitEmail();
    }

    private void sendDeniedVisitHistoryEmail() {
        try {
            var deniedVisitLinksOption = deniedVisitRepository.findAllLinkIdDistinctBySentEmailIsFalse();
            if(deniedVisitLinksOption.isPresent() && !deniedVisitLinksOption.get().isEmpty()) {
                var deniedVisits = deniedVisitLinksOption.get();
                List<JobMessage> jobMessages = new ArrayList<>();
                deniedVisits.forEach(item -> {
                    var jobMessage = new JobMessage<SendEmailDeniedVisitRequestMessage>();

                    jobMessage.setAction(JobActionConstant.SEND_EMAIL_DENIED_VISIT);
                    jobMessage.setObjectName(SendEmailDeniedVisitRequestMessage.class.getName());
                    var body = SendEmailDeniedVisitRequestMessage
                            .of(item);
                    jobMessage.setDataBody(body);
                    jobMessages.add(jobMessage);
                });

                publisher.sendMessageBatch(jobMessages);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void sendDocumentVisitEmail() {
        try {
            var viewersOption = linkStatisticRepository.findAllBySentInformationEmailIsFalse();
            if(viewersOption.isPresent() && !viewersOption.get().isEmpty()) {
                var viewers = viewersOption.get();
                List<JobMessage> jobMessages = new ArrayList<>();
                viewers.forEach(viewer -> {
                    var jobMessage = new JobMessage<SendEmailDocumentVisitRequestMessage>();

                    jobMessage.setAction(JobActionConstant.SEND_EMAIL_DOCUMENT_VISIT);
                    jobMessage.setObjectName(SendEmailDocumentVisitRequestMessage.class.getName());
                    var body = SendEmailDocumentVisitRequestMessage
                            .of(viewer.getId(), viewer.getLinkCreatorEmail(), viewer.getLinkCreatorName());
                    jobMessage.setDataBody(body);
                    jobMessages.add(jobMessage);
                });

                publisher.sendMessageBatch(jobMessages);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyTask() {
        remindInvitation();
        sendDailyActivityReport();
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void weeklyTask() {
        sendWeeklyActivityReport();
    }

    private void remindInvitation() {
        var rejectInvitationCompanyUsersOption = companyUserRepository.findAllByInvitationStatus(CompanyUserConstant.REJECT_INVITATION);
        if (rejectInvitationCompanyUsersOption.isPresent() && !rejectInvitationCompanyUsersOption.get().isEmpty()) {
            var jobMessages = new ArrayList<JobMessage>();

            var rejectInvitationCompanyUsers = rejectInvitationCompanyUsersOption.get();
            rejectInvitationCompanyUsers.forEach(companyUser -> {
                var rejectDateWithDateOnly = companyUser
                        .getRejectDate()
                        .withOffsetSameInstant(ZoneOffset.UTC)
                        .truncatedTo(ChronoUnit.DAYS);

                var now = OffsetDateTime.now(ZoneOffset.UTC)
                        .withOffsetSameInstant(ZoneOffset.UTC)
                        .truncatedTo(ChronoUnit.DAYS);

                if ((ChronoUnit.DAYS.between(rejectDateWithDateOnly, now) == TWO_DAYS ||
                        ChronoUnit.DAYS.between(rejectDateWithDateOnly, now) == EIGHT_DAYS) &&
                        companyUser.getMember_type().equals(CompanyUserConstant.INVITED_TYPE)) {
                    var jobMessage = new JobMessage<SendEmailRemindInvitationRequestMessage>();

                    jobMessage.setAction(JobActionConstant.SEND_EMAIL_REMIND_INVITATION);
                    jobMessage.setObjectName(SendEmailRemindInvitationRequestMessage.class.getName());
                    var body = SendEmailRemindInvitationRequestMessage
                            .of(companyUser.getId());
                    jobMessage.setDataBody(body);
                    jobMessages.add(jobMessage);
                }

                if (ChronoUnit.DAYS.between(rejectDateWithDateOnly, now) == ONE_DAY && companyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
                    var jobMessage = new JobMessage<SendEmailRemindTakeOwnershipRequestMessage>();

                    jobMessage.setAction(JobActionConstant.SEND_EMAIL_REMIND_TAKE_OWNER_SHIP);
                    jobMessage.setObjectName(SendEmailRemindTakeOwnershipRequestMessage.class.getName());
                    var body = SendEmailRemindTakeOwnershipRequestMessage
                            .of(companyUser.getId());
                    jobMessage.setDataBody(body);
                    jobMessages.add(jobMessage);
                }
            });

            publisher.sendMessageBatch(jobMessages);
        }
    }

    private void sendDailyActivityReport() {
        var allCompanyUserOption = companyUserRepository.findAllByStatusAndAccountIdIsNotNullAndSendDailySummaryIsTrue(CompanyUserConstant.ACTIVE_STATUS);

        if(allCompanyUserOption.isPresent() && !allCompanyUserOption.get().isEmpty()) {
            var jobMessages = new ArrayList<JobMessage>();

            allCompanyUserOption.get().forEach(item -> {
                var jobMessage = new JobMessage<SendEmailActivitiesDailySummaryRequestMessage>();

                jobMessage.setAction(JobActionConstant.SEND_EMAIL_ACTIVITIES_DAILY_SUMMARY);
                jobMessage.setObjectName(SendEmailActivitiesDailySummaryRequestMessage.class.getName());
                var body = SendEmailActivitiesDailySummaryRequestMessage
                        .of(item.getCompanyId(), item.getAccountId());
                jobMessage.setDataBody(body);
                jobMessages.add(jobMessage);
            });

            publisher.sendMessageBatch(jobMessages);
        }
    }

    private void sendWeeklyActivityReport() {
        var allCompanyUserOption = companyUserRepository.findAllByStatusAndAccountIdIsNotNullAndSendWeeklySummaryIsTrue(CompanyUserConstant.ACTIVE_STATUS);

        if(allCompanyUserOption.isPresent() && !allCompanyUserOption.get().isEmpty()) {
            var jobMessages = new ArrayList<JobMessage>();

            allCompanyUserOption.get().forEach(item -> {
                var jobMessage = new JobMessage<SendEmailActivitiesDailySummaryRequestMessage>();

                jobMessage.setAction(JobActionConstant.SEND_EMAIL_ACTIVITIES_WEEKLY_SUMMARY);
                jobMessage.setObjectName(SendEmailActivitiesDailySummaryRequestMessage.class.getName());
                var body = SendEmailActivitiesDailySummaryRequestMessage
                        .of(item.getCompanyId(), item.getAccountId());
                jobMessage.setDataBody(body);
                jobMessages.add(jobMessage);
            });

            publisher.sendMessageBatch(jobMessages);
        }
    }
}
