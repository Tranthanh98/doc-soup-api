package logixtek.docsoup.api.features.link.view.responses;

import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LinkDirectory {

    Long contentId;
    Long directoryId;
    Collection<DirectoryEntity> directories;
    Collection<FileEntity> files;
}
