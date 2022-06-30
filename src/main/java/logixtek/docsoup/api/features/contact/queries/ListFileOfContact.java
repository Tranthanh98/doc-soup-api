package logixtek.docsoup.api.features.contact.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.FileStatisticOnContact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListFileOfContact extends BaseIdentityCommand<ResponseEntity<Collection<FileStatisticOnContact>>> {

    Long contactId;

}
