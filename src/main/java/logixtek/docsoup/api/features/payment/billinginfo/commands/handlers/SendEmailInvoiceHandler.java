package logixtek.docsoup.api.features.payment.billinginfo.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.billinginfo.commands.SendEmailInvoice;
import logixtek.docsoup.api.features.share.dtos.SendEmailInvoiceRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("SendEmailInvoiceHandler")
@AllArgsConstructor
public class SendEmailInvoiceHandler implements Command.Handler<SendEmailInvoice, ResponseEntity<String>> {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final JobMessageQueuePublisher publisher;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailInvoiceHandler.class);
    @Override
    public ResponseEntity<String> handle(SendEmailInvoice command) {
        var paymentHistoryOption = paymentHistoryRepository.findById(command.getPaymentHistoryId());
        if(!paymentHistoryOption.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResource.NotFoundPaymentHistory);
        }

        var paymentHistory = paymentHistoryOption.get();
        if(paymentHistory.getSentInvoice()) {
            return ResponseEntity.accepted().build();
        }

        var jobMessage = new JobMessage<SendEmailInvoiceRequestMessage>();
        jobMessage.setDataBody(SendEmailInvoiceRequestMessage.of(command.getAccountId(), paymentHistory.getId()));
        jobMessage.setAction(JobActionConstant.SEND_EMAIL_INVOICE);
        jobMessage.setObjectName(SendEmailInvoiceRequestMessage.class.getName());

        try {
            publisher.sendMessage(jobMessage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        paymentHistory.setSentInvoice(true);
        paymentHistoryRepository.saveAndFlush(paymentHistory);

        return ResponseEntity.accepted().build();
    }
}
