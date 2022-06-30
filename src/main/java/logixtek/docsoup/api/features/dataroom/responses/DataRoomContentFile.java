package logixtek.docsoup.api.features.dataroom.responses;

import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class DataRoomContentFile {
    @Getter
    @Setter
    Long contentId;

    @Getter
    @Setter
    Boolean isActive;

    @Getter
    @Setter
    Integer orderNo;

    @Getter
    @Setter
    FileEntity file;
}
