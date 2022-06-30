package logixtek.docsoup.api.features.link.linkAccount.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;

@Data
public class UpdateLinkAccountStatus extends BaseIdentityCommand<ResponseEntity<String>> {
    @Min(1)
    Long id;

    Boolean archived;
}
