package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.SearchDirectory;
import logixtek.docsoup.api.infrastructure.models.ContentResult;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("SearchDirectoryHandler")
@AllArgsConstructor
public class SearchDirectoryHandler implements Command.Handler<SearchDirectory, ResponseEntity<PageResultOf<ContentResult>>> {
    private final DirectoryRepository directoryRepository;
    @Override
    public ResponseEntity<PageResultOf<ContentResult>> handle(SearchDirectory query) {
        var directoriesSearchResultOption = directoryRepository.findDirectoriesWithKeyword(query.getKeyword(),
                query.getCompanyId().toString(),
                query.getAccountId(),
                query.getPage(),
                query.getPageSize());

        if (directoriesSearchResultOption.isEmpty() || directoriesSearchResultOption.get().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var directoriesResult = directoriesSearchResultOption.get();

        var totalRows = directoriesResult.stream().findFirst().get().getTotalRows();

        var totalPages = (totalRows % query.getPageSize() == 0) ?
                totalRows / query.getPageSize()
                : totalRows / query.getPageSize() + 1;

        var result = PageResultOf.of(directoriesResult,
                query.getPage(),
                totalRows,
                totalPages);

        return ResponseEntity.ok(result);
    }
}
