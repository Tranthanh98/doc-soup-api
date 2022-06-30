package logixtek.docsoup.api.features.link.linkAccount.queries;

import logixtek.docsoup.api.features.link.linkAccount.responses.LinkAccountViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nullable;

@Data
public class GetListLinkAccount extends BaseIdentityCommand<ResponseEntity<LinkAccountViewModel>> {
    @Nullable
    String status;

    @Length(min = 3)
    String mode;

    @Nullable
    Boolean archived = null;
}
