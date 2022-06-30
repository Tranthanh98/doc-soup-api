package logixtek.docsoup.api.features.chatbot.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.chatbot.request.model.*;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TawkToWebhookRequest implements Command<ResponseEntity<String>> {
    String event;
    UUID chatId;
    OffsetDateTime time;
    Message message;
    Visitor visitor;
    Property property;
    Requester requester;
    Ticket ticket;
    String token;
}
