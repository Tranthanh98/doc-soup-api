package logixtek.docsoup.api.features.link.linkAccount.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.LinkAccountsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Data
@AllArgsConstructor(staticName = "of")
public class GetLinkAccountNameSuggestion extends BaseIdentityCommand<ResponseEntity<Collection<LinkAccountsEntity>>> {
    String keyword;
}
