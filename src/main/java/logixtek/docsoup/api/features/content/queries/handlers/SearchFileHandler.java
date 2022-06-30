package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.SearchFile;
import logixtek.docsoup.api.infrastructure.models.ContentResult;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("SearchFileHandler")
@AllArgsConstructor
public class SearchFileHandler implements Command.Handler<SearchFile, ResponseEntity<PageResultOf<ContentResult>>> {

    private final FileRepository fileRepository;

    @Override
    public ResponseEntity<PageResultOf<ContentResult>> handle(SearchFile query) {

        var fileSearch = fileRepository.findFileWithKeyword(query.getKeyword(),
                query.getCompanyId().toString(),
                query.getAccountId(),
                query.getPage(),
                query.getPageSize());

        if (fileSearch.isEmpty() || fileSearch.get().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var filesResult = fileSearch.get();

        var totalRows = filesResult.stream().findFirst().get().getTotalRows();

        var totalPages = (totalRows % query.getPageSize() == 0) ?
                totalRows / query.getPageSize()
                : totalRows / query.getPageSize() + 1;

        var result = PageResultOf.of(filesResult,
                query.getPage(),
                totalRows,
                totalPages);

        return ResponseEntity.ok(result);
    }
}
