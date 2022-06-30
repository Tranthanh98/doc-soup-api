package logixtek.docsoup.api.features.share.document.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.share.document.commands.ExtendDocumentLife;
import logixtek.docsoup.api.infrastructure.models.Result;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component("ExtendDocumentLifeHander")
@AllArgsConstructor
public class ExtendDocumentLifeHander implements Command.Handler<ExtendDocumentLife, Result> {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final FileRepository fileRepository;

    @SneakyThrows
    @Override
    public Result handle(ExtendDocumentLife command) {

        var fileOption = fileRepository.findById(command.getFileId());

        if(!fileOption.isPresent()){
            return new Result(false, "the file not found");
        }

        var file = fileOption.get();

        var documentOption = documentRepository.findByFileIdWithVersion(file.getId());

        if(!documentOption.isPresent()){
            return new Result(false, "the document not found");
        }

        var document = documentOption.get();

        var timeExpired = Timestamp.from(command.getExpiredAt().toInstant());

        var extendLifeResponse = documentService.ExtendLife(document.getSecureId(), String.valueOf(timeExpired.getTime()));

        if(extendLifeResponse.getSucceeded()){
            file.setDocExpiredAt(command.getExpiredAt());
            document.setExpiredAt(command.getExpiredAt());

            fileRepository.saveAndFlush(file);
            documentRepository.saveAndFlush(document);

            return new Result(true);

        }
        return new Result(false);

    }
}
