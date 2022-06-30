package logixtek.docsoup.api.features.checkin.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.commands.IRequiredUserValidationCommand;
import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.ResponseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckInCommand extends BaseIdentityCommand<ResponseEntity<String>> implements IRequiredUserValidationCommand {
    private AccountEntity account;

    @Override
    public Boolean isRequire() {
        return false;
    }
}
