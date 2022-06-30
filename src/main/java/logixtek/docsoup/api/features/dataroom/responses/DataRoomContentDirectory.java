package logixtek.docsoup.api.features.dataroom.responses;

import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class DataRoomContentDirectory {
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
    DirectoryEntity directory;
}
