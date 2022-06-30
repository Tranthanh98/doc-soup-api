package logixtek.docsoup.api.features.link.linkAccount.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.LinkAccountWithActivityInfor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor(staticName = "of")
public class GetLinkAccount extends BaseIdentityCommand<ResponseEntity<LinkAccountWithActivityInfor>> {

    @Min(1)
    Long Id;
}
