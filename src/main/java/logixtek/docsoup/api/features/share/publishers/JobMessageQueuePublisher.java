package logixtek.docsoup.api.features.share.publishers;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("JobMessageQueuePublisher")
public class JobMessageQueuePublisher {
    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    @Value("${cloud.aws.sqs.doc-soup-bg-job}")
    private String endpoint;

    private static final Logger logger = LoggerFactory.getLogger(JobMessageQueuePublisher.class);

    public void sendMessage(JobMessage jobMessage) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var message = objectMapper.writeValueAsString(jobMessage);
        if(!Strings.isNullOrEmpty(message)) {
            SendMessageRequest sendMessageRequest
                    = new SendMessageRequest(endpoint, message);
            amazonSQSAsync.sendMessage(sendMessageRequest);
        }
    }

    public void sendMessageBatch(List<JobMessage> jobMessages) {
        List <SendMessageBatchRequestEntry> messageEntries = new ArrayList<>();
        jobMessages.forEach((item) -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                var message = objectMapper.writeValueAsString(item);
                messageEntries.add(new SendMessageBatchRequestEntry().withId(UUID.randomUUID().toString()).withMessageBody(message));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        var messagesPartition = Lists.partition(messageEntries, 10);

        messagesPartition.forEach((entries) -> {
            SendMessageBatchRequest sendMessageBatchRequest
                    = new SendMessageBatchRequest(endpoint, entries);
            amazonSQSAsync.sendMessageBatch(sendMessageBatchRequest);
        });
    }
}
