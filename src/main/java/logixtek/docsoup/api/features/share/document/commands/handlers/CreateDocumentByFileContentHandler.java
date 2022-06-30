package logixtek.docsoup.api.features.share.document.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.share.document.commands.CreateDocumentByFileContent;
import logixtek.docsoup.api.features.share.document.commands.mappers.DocumentMapper;
import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentOption;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component("StreamDocumentHandler")
@AllArgsConstructor
public class CreateDocumentByFileContentHandler implements Command.Handler<CreateDocumentByFileContent, ResultOf<DocumentEntity>> {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    @SneakyThrows
    @Override
    public ResultOf<DocumentEntity> handle(CreateDocumentByFileContent command) {

        var documentOption = DocumentOption.builder()
                .docName(command.getFileName())
                .download(command.getDownload() != null && command.getDownload())
                .save(command.getSave() != null && command.getSave())
                .print(command.getPrint() != null && command.getPrint())
                .lifeSpan(command.getLifeSpan())
                .form(false)
                .build();

        var result = documentService.create(command.getFileContent(),
                command.getFileName(),
                documentOption);

        if(result.isPresent()){
            var documentEntity = DocumentMapper.INSTANCE.toEntity(result.get());
            documentEntity.setCreatedBy(command.getAccountId());
            documentEntity.setExpiredAt(command.getExpiredAt());

            var document = documentRepository.saveAndFlush(documentEntity);

            return ResultOf.of(document);
        }

        return ResultOf.of(false);
    }
}
