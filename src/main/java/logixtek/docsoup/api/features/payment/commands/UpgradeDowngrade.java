package logixtek.docsoup.api.features.payment.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotNull;

@Data
public class UpgradeDowngrade extends BaseIdentityCommand<ResponseEntity<String>> {
    Long planTierId;

    String subscriptionType;

    @NotNull
    String subscriptionId;

    String paypalPlanId;
}
