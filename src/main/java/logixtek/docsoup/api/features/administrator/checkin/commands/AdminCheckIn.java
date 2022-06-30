package logixtek.docsoup.api.features.administrator.checkin.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
public class AdminCheckIn extends BaseAdminIdentityCommand<ResponseEntity<String>> {
    String email;
}
