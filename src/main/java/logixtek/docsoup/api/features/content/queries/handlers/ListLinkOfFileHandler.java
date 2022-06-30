package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.ListLinkOfFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.LinkStatistic;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("ListLinkOfFileHandler")
@AllArgsConstructor
public class ListLinkOfFileHandler implements Command.Handler<ListLinkOfFile, ResponseEntity<PageResultOf<LinkStatistic>>> {

    private static final int MIN_PAGE = 0;
    private static final int MIN_PAGE_SIZE = 1;

    private  final LinkRepository linkRepository;
    private final FileRepository fileRepository;
    private  final PermissionService permissionService;

    @Override
    public ResponseEntity<PageResultOf<LinkStatistic>> handle(ListLinkOfFile query) {

        if(query.getPage() < MIN_PAGE || query.getPageSize() < MIN_PAGE_SIZE){
            return ResponseEntity.badRequest().build();
        }

        var fileOption = fileRepository.findById(query.getFileId());

        if(fileOption.isPresent()) {

            if(!permissionService.getOfFile(fileOption.get(), query).canRead()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Pageable pageable = PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate"));

            var resultQuery = linkRepository.findAllLinkWithStatistic(query.getFileId(), pageable);

            return ResponseEntity.ok(PageResultOf.of(resultQuery.getContent(), 
                                                    query.getPage(), 
                                                    resultQuery.getTotalElements(),
                                                    resultQuery.getTotalPages()));

        }

        return ResponseEntity.notFound().build();

    }
}
