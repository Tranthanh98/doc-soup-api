package logixtek.docsoup.api.features.share.document.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.share.document.commands.CreateDocument;
import logixtek.docsoup.api.features.share.document.commands.mappers.DocumentMapper;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentOption;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component("CreateDocumentHandler")
@AllArgsConstructor
public class CreateDocumentHandler implements Command.Handler<CreateDocument, ResultOf<UUID>> {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    @SneakyThrows
    @Override
    public ResultOf<UUID> handle(CreateDocument createDocument) {

        if (createDocument.getMultipartFile().isEmpty()) {
            return ResultOf.of(false, "No Data");
        }

        var contentType = createDocument.getMultipartFile().getContentType();

        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            return new ResultOf<UUID>(false, "Invalid file type");
        }

        var documentOption = DocumentOption.builder()
                .docName(createDocument.getDocName())
                .download(createDocument.getDownload() != null && createDocument.getDownload())
                .save(createDocument.getSave() != null && createDocument.getSave())
                .print(createDocument.getPrint() != null && createDocument.getPrint())
                .lifeSpan(createDocument.getLifeSpan())
                .form(false)
                .build();

        try {
            var result = documentService.create(createDocument.getMultipartFile(), documentOption);

            if (result.isPresent()) {
                var documentEntity = DocumentMapper.INSTANCE.toEntity(result.get());
                documentEntity.setCreatedBy(createDocument.getAccountId());
                documentEntity.setExpiredAt(createDocument.getExpiredAt());

                var document = documentRepository.saveAndFlush(documentEntity);

                return ResultOf.of(document.getId());
            }

            return ResultOf.of(false);
        } catch (IOException e) {
            return new ResultOf(false, "fail when creating a document");
        }

    }
}
