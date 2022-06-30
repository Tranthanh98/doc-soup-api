package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.ListPageReportOfFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.PageStats;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("ListPageReportOfFileHandler")
@AllArgsConstructor
public class ListPageReportOfFileHandler implements Command.Handler<ListPageReportOfFile,ResponseEntity<Collection<PageStats>>> {

    private  final FileRepository fileRepository;
    private  final PermissionService permissionService;
    @Override
    public ResponseEntity<Collection<PageStats>> handle(ListPageReportOfFile query) {

        var fileOption = fileRepository.findById(query.getFileId());

        if(fileOption.isPresent() && permissionService.getOfFile(fileOption.get(),query).canRead()) {
            var resultOption = fileRepository
                    .findAllPageStatsByFileIdAndVersion(query.getFileId(), query.getVersion());

            if (resultOption.isPresent()) {
                return ResponseEntity.ok(resultOption.get());
            }
        }
        return ResponseEntity.ok(Collections.emptyList());
    }
}
