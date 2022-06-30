package logixtek.docsoup.api.features.payment.billinginfo.queries;

import logixtek.docsoup.api.features.payment.billinginfo.responses.PayPalSubscription;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;

@Data
public class GetCurrentActivePaypalSubscription extends BaseIdentityCommand<ResponseMessageOf<PayPalSubscription>> {

}
