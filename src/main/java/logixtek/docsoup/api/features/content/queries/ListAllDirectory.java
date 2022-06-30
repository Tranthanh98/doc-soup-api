package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.features.content.responses.DirectoryViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
@NoArgsConstructor
public class ListAllDirectory extends BaseIdentityCommand<ResponseEntity<DirectoryViewModel>> {

}
