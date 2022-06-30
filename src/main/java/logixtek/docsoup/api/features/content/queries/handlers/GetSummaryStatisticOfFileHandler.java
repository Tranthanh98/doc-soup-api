package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.GetSummaryStatisticOfFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.SummaryStatisticOnFile;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("GetSummaryStatisticOfFileHandler")
@AllArgsConstructor
public class GetSummaryStatisticOfFileHandler implements Command.Handler<GetSummaryStatisticOfFile, ResponseEntity<List<SummaryStatisticOnFile>>> {

    private final FileRepository fileRepository;
    private  final PermissionService permissionService;
    @Override
    public ResponseEntity<List<SummaryStatisticOnFile>> handle(GetSummaryStatisticOfFile query) {

        var fileOption = fileRepository.findById(query.getFileId());
        if(!fileOption.isPresent())
        {
            return  ResponseEntity.notFound().build();
        }

        if(permissionService.getOfFile(fileOption.get(),query).canRead()) {
            var file = fileOption.get();

            if (!file.getAccountId().equals(query.getAccountId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            var resultOption = fileRepository.findSummaryStatisticByFileId(query.getFileId());

            if (resultOption.isPresent()) {
                return ResponseEntity.ok(resultOption.get());
            }
        }

        return  ResponseEntity.noContent().build();
    }
}
