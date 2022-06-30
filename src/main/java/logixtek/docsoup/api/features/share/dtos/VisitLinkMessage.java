package logixtek.docsoup.api.features.share.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitLinkMessage {
    Long viewerId;
    Long fileId;
    Boolean isViewing;
}
