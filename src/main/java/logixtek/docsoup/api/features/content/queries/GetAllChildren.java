package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@AllArgsConstructor(staticName = "of")
@Data
public class GetAllChildren extends BaseIdentityCommand<ResponseEntity<Collection<DirectoryEntity>>> {
    Long id;
}
