package logixtek.docsoup.api.features.link.view.responses;

import logixtek.docsoup.api.features.link.models.SecureOption;
import logixtek.docsoup.api.infrastructure.models.RoomContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@FieldNameConstants
public class LinkResult {
    Boolean ready;

    String docId;

    String watermark;

    Long viewerId;

    Long fileId;

    SecureOption secure;

    String downloadToken;

    Collection<RoomContent> directories;

    Collection<RoomContent> files;

    Integer viewType;

    String creatorEmail;

    String creatorFullName;

    String dataRoomName;

    Boolean download;

    Boolean requireVerifyEmail;

    String visitorEmail;
}
