package logixtek.docsoup.api.features.account.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
public class UpdateNotificationSetting extends BaseIdentityCommand<ResponseEntity<String>> {
    Boolean sendDailySummary;
    Boolean sendWeeklySummary;
}
