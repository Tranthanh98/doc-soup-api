package logixtek.docsoup.api.features.company.user.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
public class MakeUserMember extends BaseIdentityCommand<ResponseEntity<String>> {
    String userId;
}
