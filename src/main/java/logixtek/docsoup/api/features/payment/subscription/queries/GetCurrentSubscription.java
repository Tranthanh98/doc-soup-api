package logixtek.docsoup.api.features.payment.subscription.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.SubscriptionEntity;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetCurrentSubscription extends BaseIdentityCommand<ResponseMessageOf<SubscriptionEntity>> {
}
