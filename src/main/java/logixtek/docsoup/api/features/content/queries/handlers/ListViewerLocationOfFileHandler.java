package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.ListViewerLocationOfFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.ViewerLocation;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("ListViewerLocationOfFileHandler")
@AllArgsConstructor
public class ListViewerLocationOfFileHandler implements Command.Handler<ListViewerLocationOfFile, ResponseEntity<Collection<ViewerLocation>>> {

    private  final LinkStatisticRepository linkStatisticRepository;
    private  final FileRepository fileRepository;
    private  final PermissionService permissionService;

    @Override
    public ResponseEntity<Collection<ViewerLocation>> handle(ListViewerLocationOfFile query) {

        var fileOption = fileRepository.findById(query.getFileId());

        if(fileOption.isPresent() && permissionService.getOfFile(fileOption.get(),query).canRead()) {

            var resultOption = linkStatisticRepository.findAllViewerLocationByFileId(query.getFileId());

            if (resultOption.isPresent()) {
                return ResponseEntity.ok(resultOption.get());
            }
        }

        return  ResponseEntity.noContent().build();

    }
}
