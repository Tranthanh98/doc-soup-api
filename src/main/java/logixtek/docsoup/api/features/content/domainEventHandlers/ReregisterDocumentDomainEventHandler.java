package logixtek.docsoup.api.features.content.domainEventHandlers;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.document.commands.mappers.DocumentMapper;
import logixtek.docsoup.api.features.share.domainevents.ReregisterDocumentDomainEvent;
import logixtek.docsoup.api.infrastructure.helper.ContentHelper;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.thirdparty.Impl.StreamDocsDocumentService;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentOption;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component("RegisterDocumentDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReregisterDocumentDomainEventHandler implements Notification.Handler<ReregisterDocumentDomainEvent> {
    private final StreamDocsDocumentService documentService;
    private final DocumentRepository documentRepository;
    private final FileRepository fileRepository;
    private final FileContentRepository fileContentRepository;
    private final LinkRepository linkRepository;

    @SneakyThrows
    @Override
    public void handle(ReregisterDocumentDomainEvent domainEvent) {
        var documentData = domainEvent.getReregisterDocumentRequestMessage();

        var now = Instant.now();
        var isExpired = documentData.getExpiredAt() != null && documentData.getExpiredAt().toInstant().isBefore(now);

        if(!isExpired) {
            var fileOption = fileRepository.findById(documentData.getFileId());
            var fileContentOption = fileContentRepository.findById(documentData.getFileId());

            if(fileContentOption.isPresent() && fileOption.isPresent()){
                var documentOption = documentRepository.findById(documentData.getDocumentId());

                if(documentOption.isPresent()){
                    var file = fileOption.get();
                    var document = documentOption.get();

                    var createdDocumentOptionBuilder = DocumentOption.builder();
                    if(Boolean.TRUE.equals(documentData.getIsFile())) {
                        createdDocumentOptionBuilder
                                .docName(document.getGivenName())
                                .lifeSpan(documentData.getLifeSpan())
                                .download(true)
                                .save(true)
                                .print(true)
                                .form(false);
                    } else {
                        createdDocumentOptionBuilder
                                .docName(document.getGivenName())
                                .lifeSpan(documentData.getLifeSpan())
                                .download(documentData.getDownload())
                                .print(false)
                                .save(false)
                                .form(false);
                    }

                    var createdDocumentOption = createdDocumentOptionBuilder.build();

                    var result = documentService.create(
                            ContentHelper.convertBlobToByte(fileContentOption.get().getContent()),
                            file.getDisplayName(),
                            createdDocumentOption);

                    if(result.isPresent()){
                        //create a new document
                        var newDocument = DocumentMapper.INSTANCE.toEntity(result.get());
                        newDocument.setFileId(file.getId());
                        newDocument.setFileVersion(file.getVersion());
                        newDocument.setCreatedBy(file.getAccountId());
                        newDocument.setExpiredAt(documentData.getExpiredAt());

                        // create old version document
                        var oldDocument = DocumentMapper.INSTANCE.updateDocument(document, file);

                        documentRepository.saveAllAndFlush(new ArrayList<>(List.of(oldDocument, newDocument)));

                        var linkOption = linkRepository.findFirstByDocumentId(document.getId());

                        if(linkOption.isPresent()){
                            var link = linkOption.get();
                            link.setDocumentId(newDocument.getId());

                            linkRepository.saveAndFlush(link);
                        }

                    }
                }
            }
        }
    }
}
