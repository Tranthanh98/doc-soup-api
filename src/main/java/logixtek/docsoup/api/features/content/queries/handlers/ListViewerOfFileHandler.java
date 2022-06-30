package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.ListViewerOfFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.Viewer;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("ListViewerOfFileHandler")
@AllArgsConstructor
public class ListViewerOfFileHandler implements Command.Handler<ListViewerOfFile, ResponseEntity<PageResultOf<Viewer>>> {

    private static final int MIN_PAGE = 0;
    private static final int MIN_PAGE_SIZE = 1;

    private  final ContactRepository contactRepository;
    private  final FileRepository fileRepository;
    private  final PermissionService permissionService;

    @Override
    public ResponseEntity<PageResultOf<Viewer>> handle(ListViewerOfFile query) {

        if(query.getPage() < MIN_PAGE || query.getPageSize() < MIN_PAGE_SIZE){
            return ResponseEntity.badRequest().build();
        }

        var fileOption = fileRepository.findById(query.getFileId());

        if(fileOption.isPresent()) {

            if(!permissionService.getOfFile(fileOption.get(), query).canRead()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Pageable pageRequest = PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, "viewedAt"));
            
            var resultQuery = contactRepository.findAllViewerByFileId(query.getFileId(), pageRequest);

            return ResponseEntity.ok(PageResultOf.of(resultQuery.getContent(), 
                                                    query.getPage(), 
                                                    resultQuery.getTotalElements(),
                                                    resultQuery.getTotalPages()));
        }

         return ResponseEntity.notFound().build();

    }
}
