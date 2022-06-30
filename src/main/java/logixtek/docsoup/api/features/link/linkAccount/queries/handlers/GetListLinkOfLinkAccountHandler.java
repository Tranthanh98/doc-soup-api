package logixtek.docsoup.api.features.link.linkAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.queries.GetListLinkOfLinkAccount;
import logixtek.docsoup.api.infrastructure.constants.LinkAccountConstant;
import logixtek.docsoup.api.infrastructure.models.LinkWithStatistic;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetListLinkOfLinkAccountHandler")
@AllArgsConstructor
public class GetListLinkOfLinkAccountHandler implements Command.Handler<GetListLinkOfLinkAccount, ResponseEntity<PageResultOf<LinkWithStatistic>>> {

    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<PageResultOf<LinkWithStatistic>> handle(GetListLinkOfLinkAccount query) {

        var linkAccountOption =  linkAccountsRepository.findById(query.getLinkAccountId());
        if(!linkAccountOption.isPresent()){
            return ResponseEntity.notFound().build();
        }

        if(query.getFilterBy().equals(LinkAccountConstant.contentFilter) || query.getFilterBy().equals(LinkAccountConstant.dataRoomFilter)){
            Pageable pageable = PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate"));

            var resultQuery = linkAccountsRepository
                    .findAllLinkOfLinkAccount(query.getLinkAccountId(), query.getCompanyId(), query.getFilterBy(), pageable);

            var result = PageResultOf.of(resultQuery.getContent(),
                                                                        query.getPage(),
                                                                        resultQuery.getTotalElements(),
                                                                        resultQuery.getTotalPages());

            return ResponseEntity.ok(result);
        }

        return ResponseEntity.badRequest().build();

    }
}
