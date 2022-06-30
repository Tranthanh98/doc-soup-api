package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.content.queries.GetFilePageThumbnail;
import logixtek.docsoup.api.features.content.queries.GetLatestDocumentThumbnail;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetLatestDocumentThumbnailHandler")
@AllArgsConstructor
public class GetLatestDocumentThumbnailHandler implements Command.Handler<GetLatestDocumentThumbnail, ResponseEntity<Resource>> {

    private final FileRepository fileRepository;

    private final Pipeline pipeline;

    private final Integer FIRST_PAGE_NUMBER = 1;

    @Override
    public ResponseEntity<Resource> handle(GetLatestDocumentThumbnail query) {
        var fileOption = fileRepository
                .findFirstByAccountIdAndCompanyIdOrderByCreatedDateDesc(query.getAccountId(), query.getCompanyId());

        if(!fileOption.isPresent()){
            return ResponseEntity.notFound().build();
        }

        var file = fileOption.get();

        var commandGetThumbnail = GetFilePageThumbnail.of(file.getId(), FIRST_PAGE_NUMBER, file.getVersion());

        return commandGetThumbnail.execute(pipeline);
    }
}
