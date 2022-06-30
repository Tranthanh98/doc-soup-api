package logixtek.docsoup.api.features.company.team.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.team.queries.ListStatisticOfVisit;
import logixtek.docsoup.api.features.company.team.responses.StatisticVisitsResponse;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.models.StatisticVisits;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
@Component("ListStatisticOfVisitHandler")
public class ListStatisticOfVisitHandler implements Command.Handler<ListStatisticOfVisit, ResponseEntity<Collection<StatisticVisits>>> {

    private final CompanyUserCacheService companyUserCacheService;
    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<Collection<StatisticVisits>> handle(ListStatisticOfVisit query) {

        var companyUser = companyUserCacheService.get(query.getAccountId(),
                query.getCompanyId());
        if(companyUser == null || !companyUser.getRole().equals(RoleDefinition.C_ADMIN)){
            return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var listStatistic = linkAccountsRepository.findAllStatisticVisitByDay(
                query.getUserId(),
                query.getCompanyId().toString(),
                query.getNumOfRecentDay()
        );

        var listResult = new ArrayList<StatisticVisits>();

        for (int i = 0; i < query.getNumOfRecentDay(); i++) {
            var date = LocalDate.now().minusDays(i);
            var items = listStatistic.stream()
                    .filter(item -> item.getViewedAt().equals(date)).findAny().orElse(null);

            if(items != null){
                listResult.add(items);
            }else {
                listResult.add(StatisticVisitsResponse.of(date,0L));
            }
        }

        return ResponseEntity.ok(listResult);
    }
}
