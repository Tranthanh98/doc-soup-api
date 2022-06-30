package logixtek.docsoup.api.features.company.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.mappers.CompanyMapper;
import logixtek.docsoup.api.features.company.queries.GetCompany;
import logixtek.docsoup.api.features.company.responses.CompanyInfoWithPlanTier;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.repositories.SubscriptionEntityRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Component("GetCompanyHandler")
@AllArgsConstructor
public class GetCompanyHandler implements Command.Handler<GetCompany, ResponseMessageOf<CompanyInfoWithPlanTier>> {

    private final CompanyService companyService;
    private final PlanTierRepository planTierRepository;
    private final CompanyUserRepository companyUserRepository;
    private final SubscriptionEntityRepository subscriptionEntityRepository;
    private final LinkRepository linkRepository;

    private static final int ACTIVE_USER_STATUS = 1;
    private static final int THREE_MONTHS = 3;

    @Override
    public ResponseMessageOf<CompanyInfoWithPlanTier> handle(GetCompany query) {

       var companyOption = companyService.checkAndGetCompany(query.getId(),query.getAccountId());

       if(Boolean.TRUE.equals(companyOption.getSucceeded()))
       {
           var companyWithPlanTier = CompanyMapper.INSTANCE.toViewModel(companyOption.getData());

           var planTierOption = planTierRepository.findByIdAndIsActiveIsTrue(companyWithPlanTier.getPlanTierId());

           var totalUsers = companyUserRepository.countAllByCompanyIdAndStatusAndAccountIdIsNotNull(companyWithPlanTier.getId(), ACTIVE_USER_STATUS);

           var createdDate = OffsetDateTime.now(ZoneOffset.UTC).plusMonths(-THREE_MONTHS);

           var totalLinks = linkRepository
                   .countAllByCompanyIdAndStatusAndCreatedDateAfter(query.getCompanyId(), LinkConstant.ACTIVE_STATUS, createdDate);

           var totalVisits = linkRepository.sumVisit(query.getCompanyId(), createdDate);

           companyWithPlanTier.setTotalLinks(totalLinks);
           companyWithPlanTier.setTotalVisits(totalVisits);

           if(!planTierOption.isPresent()){
               return new ResponseMessageOf<>(HttpStatus.INTERNAL_SERVER_ERROR);
           }

           companyWithPlanTier.setTotalUsers(totalUsers);
           companyWithPlanTier.setPlanTier(planTierOption.get());

           var currentActiveSubscriptionOption = subscriptionEntityRepository.findById(companyOption.getData().getId());
           if(currentActiveSubscriptionOption.isPresent()) {
               companyWithPlanTier.setCurrentSubscriptionType(currentActiveSubscriptionOption.get().getSubType());
           }

           return  ResponseMessageOf.of(HttpStatus.OK, companyWithPlanTier);
       }

       return  ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));

    }
}
