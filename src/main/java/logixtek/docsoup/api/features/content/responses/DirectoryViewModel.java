package logixtek.docsoup.api.features.content.responses;

import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class DirectoryViewModel {
    Collection<DirectoryEntity> team;
    Collection<DirectoryEntity> privacy;
}
