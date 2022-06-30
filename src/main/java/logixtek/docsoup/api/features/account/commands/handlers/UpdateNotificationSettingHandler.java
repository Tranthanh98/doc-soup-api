package logixtek.docsoup.api.features.account.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.account.commands.UpdateNotificationSetting;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("UpdateNotificationSettingHandler")
@AllArgsConstructor
public class UpdateNotificationSettingHandler implements Command.Handler<UpdateNotificationSetting, ResponseEntity<String>> {
    private final AccountService accountService;

    @Override
    public ResponseEntity<String> handle(UpdateNotificationSetting command) {
        var account = accountService.get(command.getAccountId());
        if(account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResource.NotFoundAccount);
        }

        if(account.getSendDailySummary().equals(command.getSendDailySummary()) &&
                account.getSendWeeklySummary().equals(command.getSendWeeklySummary())) {
            return ResponseEntity.accepted().build();
        }

        account.setSendDailySummary(command.getSendDailySummary());
        account.setSendWeeklySummary(command.getSendWeeklySummary());

        accountService.update(account);

        return ResponseEntity.accepted().build();
    }
}
