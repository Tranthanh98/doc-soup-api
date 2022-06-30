package logixtek.docsoup.api.infrastructure.models;

import lombok.Data;

@Data
public class JobMessage<T> {
    String action;
    String objectName;
    T dataBody;
}
