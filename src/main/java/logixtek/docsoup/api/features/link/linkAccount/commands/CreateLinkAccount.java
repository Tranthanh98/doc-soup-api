package logixtek.docsoup.api.features.link.linkAccount.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

@Data
@FieldNameConstants
public class CreateLinkAccount extends BaseIdentityCommand<ResponseEntity<Long>> {
    @Length(min = 1, max = 150)
    String name;
}
