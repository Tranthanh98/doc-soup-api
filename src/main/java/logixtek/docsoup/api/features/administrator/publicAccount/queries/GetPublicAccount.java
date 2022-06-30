package logixtek.docsoup.api.features.administrator.publicAccount.queries;

import logixtek.docsoup.api.features.administrator.publicAccount.responses.PublicAccountDetail;
import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Data
public class GetPublicAccount extends BaseAdminIdentityCommand<ResponseEntity<PublicAccountDetail>> {

    String accountId;

}
