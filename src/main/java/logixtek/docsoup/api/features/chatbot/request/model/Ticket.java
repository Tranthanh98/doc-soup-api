package logixtek.docsoup.api.features.chatbot.request.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Ticket {
    UUID id;
    Long humanId;
    String subject;
    String message;
}
