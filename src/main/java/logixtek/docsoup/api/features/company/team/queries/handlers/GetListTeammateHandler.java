package logixtek.docsoup.api.features.company.team.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.team.queries.GetListTeammate;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.models.TeammateStatistic;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component("GetListTeammateHandler")
public class GetListTeammateHandler implements Command.Handler<GetListTeammate, ResponseEntity<PageResultOf<TeammateStatistic>>> {

    private final CompanyUserRepository companyUserRepository;
    private final CompanyUserCacheService companyUserCacheService;

    @Override
    public ResponseEntity<PageResultOf<TeammateStatistic>> handle(GetListTeammate query) {

        var companyUser = companyUserCacheService.get(query.getAccountId(),
                query.getCompanyId());

        if(companyUser == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var usersWithStatisticOption = companyUserRepository.findTeammateWithStatisticData(
                query.getNumOfRecentDay(),
                query.getCompanyId().toString(),
                query.getPage(),
                query.getPageSize());

        if (usersWithStatisticOption.isEmpty() || usersWithStatisticOption.get().isEmpty()) {
            return ResponseEntity.ok(PageResultOf.empty());
        }

        var totalRows =  usersWithStatisticOption.get().stream().findFirst().get().getTotalRows();

        var totalPages = (int) ((totalRows % query.getPageSize() == 0) ?
                totalRows / query.getPageSize()
                : totalRows / query.getPageSize() + 1);

        var result = PageResultOf.of(usersWithStatisticOption.get(),
                query.getPage(),
                totalRows,
                totalPages);
        return ResponseEntity.ok(result);
    }
}
