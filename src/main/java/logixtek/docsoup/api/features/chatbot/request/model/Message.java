package logixtek.docsoup.api.features.chatbot.request.model;

import lombok.Data;

@Data
public class Message {
    String text;
    String type;
    Sender sender;
}
