package logixtek.docsoup.api.features.dataroom.responses;

import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DataRoomChildContent {
    @Getter
    @Setter
    private List<DirectoryEntity> directories;

    @Getter
    @Setter
    private List<FileEntity> files;
}
