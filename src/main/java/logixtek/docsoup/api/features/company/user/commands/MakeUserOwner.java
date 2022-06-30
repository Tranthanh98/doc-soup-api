package logixtek.docsoup.api.features.company.user.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
public class MakeUserOwner extends BaseIdentityCommand<ResponseEntity<String>> {
    String targetAccountId;
}
