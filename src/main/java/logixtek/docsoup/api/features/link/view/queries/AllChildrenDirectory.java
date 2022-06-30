package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.UUID;

@Data
@Builder
public class AllChildrenDirectory implements Command<ResponseEntity<Collection<DirectoryEntity>>> {
    UUID linkId;
    String deviceId;
    Long viewerId ;
    Long directoryId;
}
