package logixtek.docsoup.api.features.share.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.share.commands.UploadFileCommand;
import logixtek.docsoup.api.features.share.document.commands.CreateDocument;
import logixtek.docsoup.api.features.share.domainevents.CreateDirectoryDomainEvent;
import logixtek.docsoup.api.features.share.mappers.FileEntityMapper;
import logixtek.docsoup.api.features.share.services.ContentLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.entities.FileContentEntity;
import logixtek.docsoup.api.infrastructure.helper.ContentHelper;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Component("UploadFileHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UploadFileHandler implements Command.Handler<UploadFileCommand, ResponseMessageOf<Long>>{

    @Value("${technet.streamdocs.document.lifespan}")
    private String lifeSpan;

    @Value("${docsoup.file.lifespan}")
    private Integer yearLifeSpan;

    private static final String ROOT_FOLDER_NAME = "Root Folder";

    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;
    private final FileContentRepository fileContentRepository;
    private final DocumentRepository documentRepository;

    private final PermissionService permissionService;
    private final ContentLimitationService contentLimitationService;

    private static final Logger logger = LoggerFactory.getLogger(UploadFileHandler.class);

    private final Pipeline pipeline;

    @Override
    public ResponseMessageOf<Long> handle(UploadFileCommand command) {

        if (!contentLimitationService.isAllowWithPageNumber(command.getCompanyId(), command.getAccountId(),
                command.getMultipartFile())) {
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY,
                    ResponseResource.LimitedPlanExceeded, Map.of());
        }

        if(command.getMultipartFile().isEmpty()){
           return new ResponseMessageOf<Long>(HttpStatus.BAD_REQUEST,"NO data",
                   Map.of(UploadFileCommand.Fields.multipartFile, "NO DATA"));
        }

        var contentType = command.getMultipartFile().getContentType();

        if(contentType == null || !contentType.equalsIgnoreCase("application/pdf")){
            return new ResponseMessageOf<Long>(HttpStatus.BAD_REQUEST,"Invalid file type",
                    Map.of(UploadFileCommand.Fields.multipartFile, "Invalid file type"));
        }

        Optional<DirectoryEntity> directoryOption;

        if(command.getDirectoryId() == null){
            directoryOption = getFirstDirectory(command);

            if(directoryOption.isEmpty()){
                raiseCreateRootFolderDomainEvent(command);
                directoryOption = getFirstDirectory(command);
            }
        }
        else{
            directoryOption = directoryRepository.findById(command.getDirectoryId());
        }

        if(directoryOption.isEmpty())
        {
            return ResponseMessageOf.ofBadRequest("Directory does not exist",
                    Map.of(UploadFileCommand.Fields.directoryId, "Directory does not exist"));
        }

        var directory = directoryOption.get();

        if(!permissionService.get(directory,command).canWrite())
        {
            return new ResponseMessageOf<Long>(HttpStatus.FORBIDDEN);
        }

        var docExpiredAt = OffsetDateTime.now(ZoneOffset.UTC).plusYears(yearLifeSpan);
        var result = createDocument(command,docExpiredAt);

        if(result.getSucceeded()){

            var documentOption = documentRepository
                    .findById(result.getData());

            if(documentOption.isPresent()){
                return saveFile(command, documentOption.get(), docExpiredAt, directory.getId());
            }
        }

        return ResponseMessageOf.of(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseMessageOf<Long> saveFile(UploadFileCommand command, DocumentEntity document, OffsetDateTime documentExpiredAt, Long directoryId){

        var fileName = command.getMultipartFile().getOriginalFilename();
        var extension = fileName != null ? fileName.substring(fileName.lastIndexOf(".")): ".pdf";

        var displayName = command.getDisplayName() == null ||
                command.getDisplayName().isEmpty() ?
                command.getMultipartFile().getOriginalFilename() :
                command.getDisplayName();

        var checkFileName = fileRepository.findByDisplayNameAndDirectoryIdAndCompanyId(
                displayName, directoryId, command.getCompanyId());

        if(checkFileName.isPresent())
        {
            displayName = displayName + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        }

        try {
            var fileContent = ContentHelper.convertFileToBlob(command.getMultipartFile());

            var fileEntity = FileEntityMapper.INSTANCE.toEntity(command);
            fileEntity.setDisplayName(displayName);
            fileEntity.setExtension(extension);
            fileEntity.setSize(command.getMultipartFile().getSize());
            fileEntity.setName(displayName);
//            fileEntity.setDocumentId(documentId);
            fileEntity.setDocExpiredAt(documentExpiredAt);
            fileEntity.setDirectoryId(directoryId);

            if (!contentLimitationService.isAllowWithPageNumber(command.getCompanyId(), command.getAccountId(),
                    command.getMultipartFile())) {
                return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY,
                        ResponseResource.LimitedPlanExceeded, Map.of());
            }
            
            var fileResult = fileRepository.saveAndFlush(fileEntity);

            if (fileResult.getId() > 0) {

                var fileContentEntity = FileContentEntity.of(fileResult.getId(),fileContent,command.getAccountId());

                fileContentRepository.saveAndFlush(fileContentEntity);

                document.setFileId(fileResult.getId());
                document.setFileVersion(fileResult.getVersion());

                documentRepository.saveAndFlush(document);

                return  ResponseMessageOf.of(HttpStatus.CREATED,fileResult.getId());
            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
            return new ResponseMessageOf<Long>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return  new ResponseMessageOf<Long>(HttpStatus.UNPROCESSABLE_ENTITY);

    }

    private ResultOf<UUID> createDocument(UploadFileCommand command, OffsetDateTime expiredAt){
        var displayName = command.getDisplayName() == null ||
                command.getDisplayName().isEmpty() ?
                command.getMultipartFile().getOriginalFilename() :
                command.getDisplayName();

        var documentCommand = CreateDocument.builder()
                .docName(displayName)
                .lifeSpan(lifeSpan)
                .download(true)
                .multipartFile(command.getMultipartFile())
                .print(true)
                .save(true)
                .expiredAt(expiredAt)
                .build();

        documentCommand.setAccountId(command.getAccountId());

        return documentCommand.execute(pipeline);
    }

    private void raiseCreateRootFolderDomainEvent(UploadFileCommand command){
        var domainEvent = CreateDirectoryDomainEvent.of(0,
                ROOT_FOLDER_NAME,
                false,
                command.getAccountId(),
                command.getCompanyId());

        domainEvent.send(pipeline);

    }

    private Optional<DirectoryEntity> getFirstDirectory(UploadFileCommand command){
        return directoryRepository
                .findFirstByAccountIdAndParentIdAndCompanyIdAndIsTeam(command.getAccountId(),
                        0L,
                        command.getCompanyId(),
                        false);
    }
}
