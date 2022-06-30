package logixtek.docsoup.api.infrastructure.models;

import lombok.Data;

@Data
public class WebSocketMessage<T> {
    String action;
    T dataBody;
}
