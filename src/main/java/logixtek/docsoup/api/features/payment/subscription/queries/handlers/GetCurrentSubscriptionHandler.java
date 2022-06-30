package logixtek.docsoup.api.features.payment.subscription.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.subscription.queries.GetCurrentSubscription;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.entities.SubscriptionEntity;
import logixtek.docsoup.api.infrastructure.repositories.SubscriptionEntityRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("GetCurrentSubscriptionHandler")
@AllArgsConstructor
public class GetCurrentSubscriptionHandler implements Command.Handler<GetCurrentSubscription, ResponseMessageOf<SubscriptionEntity>> {
    private final CompanyUserCacheService companyUserCacheService;
    private final SubscriptionEntityRepository subscriptionEntityRepository;

    @Override
    public ResponseMessageOf<SubscriptionEntity> handle(GetCurrentSubscription command) {
        var companyUser = companyUserCacheService.get(command.getAccountId(), command.getCompanyId());

        if (companyUser == null) {
            return ResponseMessageOf.ofBadRequest(ResponseResource.NotFoundCompany, Map.of());
        }

        if (companyUser.getMember_type() != CompanyUserConstant.OWNER_TYPE) {
            return ResponseMessageOf.ofBadRequest(ResponseResource.DonNotHavePermission, Map.of());
        }

        var result = subscriptionEntityRepository.findById(companyUser.getCompanyId());
        if(result.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.OK, result.get());
        }

        return ResponseMessageOf.of(HttpStatus.OK);
    }
}
