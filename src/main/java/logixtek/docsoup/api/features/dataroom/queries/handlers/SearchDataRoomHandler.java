package logixtek.docsoup.api.features.dataroom.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.queries.SearchDataRoom;
import logixtek.docsoup.api.infrastructure.models.SimplifiedDataRoomInfo;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("SearchDataRoomHandler")
@AllArgsConstructor
public class SearchDataRoomHandler implements Command.Handler<SearchDataRoom, ResponseEntity<PageResultOf<SimplifiedDataRoomInfo>>> {

    private final DataRoomRepository dataRoomRepository;

    @Override
    public ResponseEntity<PageResultOf<SimplifiedDataRoomInfo>> handle(SearchDataRoom query) {

        Pageable pageRequest = PageRequest.of(query.getPage(),
                query.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdDate"));

        var resultQuery = dataRoomRepository
                .searchDataRoom(query.getKeyword(),
                        query.getCompanyId(),
                        query.getAccountId(),
                        pageRequest);

        return ResponseEntity.ok(PageResultOf.of(resultQuery.getContent(),
                query.getPage(),
                resultQuery.getTotalElements(),
                resultQuery.getTotalPages()));

    }
}
