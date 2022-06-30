package logixtek.docsoup.api.features.link.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.queries.GetTotalLinks;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetTotalLinksHandler")
@AllArgsConstructor
public class GetTotalLinksHandler implements Command.Handler<GetTotalLinks, ResponseEntity<Integer>> {
    private final LinkRepository linkRepository;
    @Override
    public ResponseEntity<Integer> handle(GetTotalLinks command) {
        var result = linkRepository.countAllByCompanyIdAndStatus(command.getCompanyId(), LinkConstant.ACTIVE_STATUS);

        return ResponseEntity.ok(result);
    }
}
