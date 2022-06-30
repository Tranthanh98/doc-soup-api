package logixtek.docsoup.api.features.share.consumers;

import an.awesome.pipelinr.Pipeline;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.DeleteDocumentDomainEvent;
import logixtek.docsoup.api.features.share.domainevents.ExportDataRoomViewerDomainEvent;
import logixtek.docsoup.api.features.share.domainevents.ExportFileVisitorDomainEvent;
import logixtek.docsoup.api.features.share.domainevents.ReregisterDocumentDomainEvent;
import logixtek.docsoup.api.features.share.domainevents.sendemail.*;
import logixtek.docsoup.api.features.share.dtos.*;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JobMessageQueueConsumer {

    private final Pipeline pipeline;

    @SqsListener(value = "${cloud.aws.sqs.doc-soup-bg-job}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveMessage(String message) throws IOException, ClassNotFoundException {
        if(!Strings.isNullOrEmpty(message)) {
            ObjectMapper objectMapper = new ObjectMapper();
            var jobMessage = objectMapper.readValue(message, JobMessage.class);
            if(jobMessage.getAction().equals(JobActionConstant.EXPORT_DATA_ROOM_VIEWER)) {
                var body = jobMessage.getDataBody();
                DataRoomViewerRequestMessage dataRoomViewerRequest = convertToClass(body, DataRoomViewerRequestMessage.class);
                var exportDataRoomViewerDomainEvent = ExportDataRoomViewerDomainEvent
                        .of(dataRoomViewerRequest.getAccountId(), dataRoomViewerRequest.getDataRoomId());
                exportDataRoomViewerDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.NEW_DOCUMENT_VERSION)) {
                var body = jobMessage.getDataBody();
                ReregisterDocumentRequestMessage registerDocumentRequestMessage = convertToClass(body, ReregisterDocumentRequestMessage.class);
                var exportDataRoomViewerDomainEvent = ReregisterDocumentDomainEvent.of(registerDocumentRequestMessage);
                exportDataRoomViewerDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.DELETE_DOCUMENT)) {
                var body = jobMessage.getDataBody();
                String secureId = convertToClass(body, String.class);
                var deleteDocumentDomainEvent = new DeleteDocumentDomainEvent();
                deleteDocumentDomainEvent.setSecureId(secureId);
                deleteDocumentDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_INVITATION)) {
                var body = jobMessage.getDataBody();
                SendEmailInvitationRequestMessage sendEmailInvitationRequest = convertToClass(body, SendEmailInvitationRequestMessage.class);
                var sendEmailInvitationDomainEvent = new SendEmailInvitationDomainEvent();
                sendEmailInvitationDomainEvent.setSendEmailInvitationRequest(sendEmailInvitationRequest);
                sendEmailInvitationDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_RESET_PASSWORD)) {
                var body = jobMessage.getDataBody();
                SendEmailResetPasswordRequestMessage requestMessage = convertToClass(body, SendEmailResetPasswordRequestMessage.class);
                var sendEmailResetPasswordDomainEvent = SendEmailResetPasswordDomainEvent.of(requestMessage.getAccount());
                sendEmailResetPasswordDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_DATA_ROOM_VISIT_LINK)) {
                var body = jobMessage.getDataBody();
                SendEmailDataRoomVisitLinkRequestMessage requestMessage = convertToClass(body, SendEmailDataRoomVisitLinkRequestMessage.class);
                var sendEmailDataRoomVisitLinkDomainEvent = SendEmailDataRoomVisitLinkDomainEvent.of(requestMessage);
                sendEmailDataRoomVisitLinkDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_INVOICE)) {
                var body = jobMessage.getDataBody();
                SendEmailInvoiceRequestMessage requestMessage = convertToClass(body, SendEmailInvoiceRequestMessage.class);
                var sendEmailInvoiceDomainEvent = SendEmailInvoiceDomainEvent.of(requestMessage.getAccountId(), requestMessage.getPaymentHistoryId());
                sendEmailInvoiceDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_PAID_REMIND)) {
                var body = jobMessage.getDataBody();
                SendEmailPaidRemindRequestMessage requestMessage = convertToClass(body, SendEmailPaidRemindRequestMessage.class);
                var sendEmailPaidRemindDomainEvent = SendEmailPaidRemindDomainEvent.of(requestMessage.getPaymentHistoryId());
                sendEmailPaidRemindDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_PAID_ENDED)) {
                var body = jobMessage.getDataBody();
                SendEmailPaymentPaidEndedRequestMessage requestMessage = convertToClass(body, SendEmailPaymentPaidEndedRequestMessage.class);
                var sendEmailPaymentPaidEndedDomainEvent = SendEmailPaymentPaidEndedDomainEvent.of(requestMessage.getPaymentHistoryId(), requestMessage.getPreviousPlanTierId());
                sendEmailPaymentPaidEndedDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_DISABLE_LINK)) {
                var body = jobMessage.getDataBody();
                SendEmailDisableLinkRequestMessage requestMessage = convertToClass(body, SendEmailDisableLinkRequestMessage.class);
                var sendEmailDisableLinkDomainEvent = SendEmailDisableLinkDomainEvent.of(requestMessage);
                sendEmailDisableLinkDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_VALIDATION_EMAIL_LINK)) {
                var body = jobMessage.getDataBody();
                SendEmailVerifyEmailLinkRequestMessage requestMessage = convertToClass(body, SendEmailVerifyEmailLinkRequestMessage.class);
                var sendEmailValidationEmailLinkDomainEvent = SendEmailVerifyEmailLinkDomainEvent.of(requestMessage);
                sendEmailValidationEmailLinkDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_DOCUMENT_VISIT)) {
                var body = jobMessage.getDataBody();
                SendEmailDocumentVisitRequestMessage requestMessage = convertToClass(body, SendEmailDocumentVisitRequestMessage.class);
                var sendEmailDocumentVisitDomainEvent = SendEmailDocumentVisitDomainEvent.of(requestMessage);
                sendEmailDocumentVisitDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_DENIED_VISIT)) {
                var body = jobMessage.getDataBody();
                SendEmailDeniedVisitRequestMessage requestMessage = convertToClass(body, SendEmailDeniedVisitRequestMessage.class);
                var sendEmailDeniedVisitDomainEvent = SendEmailDeniedVisitDomainEvent.of(requestMessage);
                sendEmailDeniedVisitDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.EXPORT_FILE_VISITOR)) {
                var body = jobMessage.getDataBody();
                ExportFileVisitorRequestMessage requestMessage = convertToClass(body, ExportFileVisitorRequestMessage.class);
                var exportFileVisitorDomainEvent = ExportFileVisitorDomainEvent.of(requestMessage.getAccountId(), requestMessage.getFileId());
                exportFileVisitorDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_FILE_VISITOR)) {
                var body = jobMessage.getDataBody();
                SendEmailExportFileVisitorRequestMessage requestMessage = convertToClass(body, SendEmailExportFileVisitorRequestMessage.class);
                var sendEmailExportFileVisitorDomainEvent = SendEmailExportFileVisitorDomainEvent.of(requestMessage);
                sendEmailExportFileVisitorDomainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_ACCEPTED_INVITATION)) {
                var body = jobMessage.getDataBody();
                SendEmailAcceptedInvitationRequestMessage requestMessage = convertToClass(body, SendEmailAcceptedInvitationRequestMessage.class);
                var domainEvent = SendEmailAcceptedInvitationDomainEvent.of(requestMessage);
                domainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_REMIND_INVITATION)) {
                var body = jobMessage.getDataBody();
                SendEmailRemindInvitationRequestMessage requestMessage = convertToClass(body, SendEmailRemindInvitationRequestMessage.class);
                var domainEvent = SendEmailRemindInvitationDomainEvent.of(requestMessage);
                domainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_REMIND_TAKE_OWNER_SHIP)) {
                var body = jobMessage.getDataBody();
                SendEmailRemindTakeOwnershipRequestMessage requestMessage = convertToClass(body, SendEmailRemindTakeOwnershipRequestMessage.class);
                var domainEvent = SendEmailRemindTakeOwnershipDomainEvent.of(requestMessage);
                domainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_VISITED_DOCUMENT_SAMPLE)) {
                var body = jobMessage.getDataBody();
                SendEmailVisitedDocumentSampleRequestMessage requestMessage = convertToClass(body, SendEmailVisitedDocumentSampleRequestMessage.class);
                var domainEvent = SendEmailVisitedDocumentSampleDomainEvent.of(requestMessage);
                domainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_ACTIVITIES_DAILY_SUMMARY)) {
                var body = jobMessage.getDataBody();
                SendEmailActivitiesDailySummaryRequestMessage requestMessage = convertToClass(body, SendEmailActivitiesDailySummaryRequestMessage.class);
                var domainEvent = SendEmailActivitiesDailySummaryDomainEvent.of(requestMessage);
                domainEvent.send(pipeline);
            }

            if(jobMessage.getAction().equals(JobActionConstant.SEND_EMAIL_ACTIVITIES_WEEKLY_SUMMARY)) {
                var body = jobMessage.getDataBody();
                SendEmailActivitiesWeeklySummaryRequestMessage requestMessage = convertToClass(body, SendEmailActivitiesWeeklySummaryRequestMessage.class);
                var domainEvent = SendEmailActivitiesWeeklySummaryDomainEvent.of(requestMessage);
                domainEvent.send(pipeline);
            }
        }
    }


    @SneakyThrows
    private <T> T convertToClass(Object object, Class<T> classType){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var result = (T) mapper.convertValue(object, classType);

        return result;
    }
}
