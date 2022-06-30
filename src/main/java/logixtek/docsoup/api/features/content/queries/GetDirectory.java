package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;
import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class GetDirectory extends BaseIdentityCommand<ResponseEntity<List<DirectoryEntity>>> {
   
    @Getter
    @NonNull
    @Min(1)
    private Long id;

}
