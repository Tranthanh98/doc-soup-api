package logixtek.docsoup.api.features.link.view.queries;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public class BaseViewFile
{
    UUID linkId;
    Long contentId;
    Long fileId;
    String deviceId;
    Long viewerId ;
}
