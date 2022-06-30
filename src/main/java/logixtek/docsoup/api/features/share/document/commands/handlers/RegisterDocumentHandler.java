package logixtek.docsoup.api.features.share.document.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.share.document.commands.CreateDocumentByFileContent;
import logixtek.docsoup.api.features.share.document.commands.RegisterDocument;
import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.helper.ContentHelper;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component("RegisterDocumentHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegisterDocumentHandler implements Command.Handler<RegisterDocument, ResultOf<UUID>> {

    @Value("${technet.streamdocs.document.lifespan}")
    private String lifeSpan;

    @Value("${docsoup.file.lifespan}")
    private Integer yearLifeSpan;

    private final FileRepository fileRepository;
    private final DocumentRepository documentRepository;
    private final FileContentRepository fileContentRepository;

    private final Pipeline pipeline;


    @SneakyThrows
    @Override
    public ResultOf<UUID> handle(RegisterDocument command) {

         var fileOption = fileRepository.findById(command.getFileId());

         if(!fileOption.isPresent()){
             return ResultOf.of(false, "file not found");
         }

         var file = fileOption.get();

         var document = CreateDocument(file, command);

         if(!document.getSucceeded()){
             return ResultOf.of(false, document.getMessage());
         }

         return ResultOf.of(document.getData().getId());

    }

    
    private ResultOf<DocumentEntity> CreateDocument(FileEntity file,
                                             RegisterDocument registerDocument
                                            ) throws SQLException {

        var fileContentOption = fileContentRepository.findById(file.getId());

        if(fileContentOption.isPresent()){

            var requestExpiredDate = getExpiredAt(registerDocument.getExpiredAt());

            byte[] byteData = ContentHelper.convertBlobToByte(fileContentOption.get().getContent());

            var documentLifeSpan = lifeSpan;
            if(registerDocument.getExpiredAt() != null) {
                documentLifeSpan = Utils.calculateLifeSpan(Instant.now(), registerDocument.getExpiredAt().toInstant());
            }

            var createDocumentCommand = CreateDocumentByFileContent.builder()
                    .lifeSpan(documentLifeSpan)
                    .download(registerDocument.getDownload())
                    .save(false)
                    .print(false)
                    .fileContent(byteData)
                    .fileName(file.getName()+file.getExtension())
                    .expiredAt(requestExpiredDate)
                    .build();

            createDocumentCommand.setAccountId(registerDocument.getAccountId());

            var resultCreateDocument = createDocumentCommand.execute(pipeline);

            if(!resultCreateDocument.getSucceeded()){
                return ResultOf.of(false, "create document fail");
            }

            var document = resultCreateDocument.getData();

            var oldDocument = documentRepository.findFirstByFileIdAndFileVersionAndRefIdIsNull(file.getId(), file.getVersion());
            if(oldDocument.isPresent()){
                document.setRefId(oldDocument.get().getId());
            }
            
            documentRepository.saveAndFlush(document);

            return ResultOf.of(document);
        }

        return ResultOf.of(false, "file content not found");
    }



    private OffsetDateTime getExpiredAt(OffsetDateTime requestExpired){

        if(requestExpired !=null)
        {
            return requestExpired;
        }

        return OffsetDateTime.now(ZoneOffset.UTC).plusYears(yearLifeSpan);

    }

}
