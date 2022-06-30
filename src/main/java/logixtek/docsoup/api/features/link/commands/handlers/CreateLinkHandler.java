package logixtek.docsoup.api.features.link.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.commands.CreateLink;
import logixtek.docsoup.api.features.link.mappers.LinkMapper;
import logixtek.docsoup.api.features.share.document.commands.RegisterDocument;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.WatermarkRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Component("CreateLinkHandler")
@AllArgsConstructor
public class CreateLinkHandler implements Command.Handler<CreateLink, ResponseMessageOf<UUID>> {

    private final Pipeline pipeline;
    private final LinkRepository linkEntityRepository;
    private final FileRepository fileRepository;
    private  final DataRoomRepository dataRoomRepository;
    private  final WatermarkRepository watermarkRepository;
    private  final PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;

     private static final int  fileLink = 0;
    private static final int  dataRoomLink = 1;
    @Override
    public ResponseMessageOf<UUID> handle(CreateLink createLink) {


        var linkEntity = LinkMapper.INSTANCE.toEntity(createLink);

        if(createLink.getLinkType()==dataRoomLink)
        {
            if(!dataRoomLimitationService.isAllow(createLink.getCompanyId())){
                return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded,
                        Map.of());
            }

            var dataRoomOption = dataRoomRepository.findById(createLink.getResourceId());
            if(!dataRoomOption.isPresent())
            {
                return ResponseMessageOf.ofBadRequest("Not found data room",
                        Map.of(CreateLink.Fields.resourceId, "Not found data room"));
            }

            if(!permissionService.getOfDataRoom(dataRoomOption.get(),createLink).canWrite())
            {
                return  ResponseMessageOf.of(HttpStatus.FORBIDDEN);
            }

            linkEntity.setCompanyId(dataRoomOption.get().getCompanyId());
        }

        if(createLink.getLinkType()==fileLink) {

            var fileOption = fileRepository
                    .findById(createLink.getResourceId());

            if(!fileOption.isPresent()){
                return ResponseMessageOf.ofBadRequest("File not found",
                        Map.of(CreateLink.Fields.resourceId, "File not found"));
            }

            var file = fileOption.get();

            if(permissionService.getOfFile(file,createLink).isDenied())
            {
                return  ResponseMessageOf.of(HttpStatus.FORBIDDEN);
            }

            linkEntity.setCompanyId(file.getCompanyId());

            var registerDocumentCommand = RegisterDocument.builder()
                    .fileId(createLink.getResourceId())
                    .download(createLink.getDownload())
                    .expiredAt(createLink.getExpiredAt())
                    .build();

            if(createLink.getExpiredAt()!= null)
            {
               registerDocumentCommand.setExpiredAt(createLink.getExpiredAt().withOffsetSameInstant(ZoneOffset.UTC));
            }


            registerDocumentCommand.setAccountId(createLink.getAccountId());

            var resultRegister = registerDocumentCommand.execute(pipeline);

            if (!resultRegister.getSucceeded()) {
                return ResponseMessageOf.ofBadRequest(resultRegister.getMessage(), Map.of());
            }

            linkEntity.setDocumentId(resultRegister.getData());
        }

        if(createLink.getWatermarkId()!=null)
        {
            var watermarkOption =watermarkRepository.findById(createLink.getWatermarkId());
            if(!watermarkOption.isPresent())
            {
                return ResponseMessageOf.ofBadRequest("Not found watermark",
                        Map.of(CreateLink.Fields.resourceId, "Not found watermark"));
            }
            var watermark = watermarkOption.get();

            if(!watermark.getAccountId().equals(createLink.accountId))
            {
                return  ResponseMessageOf.of(HttpStatus.FORBIDDEN);
            }

            linkEntity.setWatermarkId(createLink.getWatermarkId());

        }

        var resultSaveLink = linkEntityRepository.saveAndFlush(linkEntity);

        return ResponseMessageOf.of(HttpStatus.CREATED, resultSaveLink.getId());
    }
}
