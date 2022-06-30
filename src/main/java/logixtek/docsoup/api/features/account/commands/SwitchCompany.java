package logixtek.docsoup.api.features.account.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Data
public class SwitchCompany extends BaseIdentityCommand<ResponseEntity<String>> {
    UUID destinationCompanyId;
}
