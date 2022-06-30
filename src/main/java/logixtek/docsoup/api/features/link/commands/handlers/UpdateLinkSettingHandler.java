package logixtek.docsoup.api.features.link.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import com.fasterxml.jackson.databind.ObjectMapper;
import logixtek.docsoup.api.features.link.commands.UpdateLinkSetting;
import logixtek.docsoup.api.features.link.mappers.LinkMapper;
import logixtek.docsoup.api.features.link.models.SecureValue;
import logixtek.docsoup.api.features.share.document.commands.DeleteDocument;
import logixtek.docsoup.api.features.share.document.commands.RegisterDocument;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UpdateLinkSettingHandler")
@AllArgsConstructor
public class UpdateLinkSettingHandler implements Command.Handler<UpdateLinkSetting, ResponseMessageOf<String>> {

    private final Pipeline pipeline;
    private final LinkRepository linkEntityRepository;
    private final LinkStatisticRepository linkStatisticRepository;
    private final  PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;
    private final DocumentService documentService;

    private static final Logger logger = LoggerFactory.getLogger(UpdateLinkSettingHandler.class);

    @Override
    public ResponseMessageOf<String> handle(UpdateLinkSetting command) {

        var linkOption = linkEntityRepository.findById(command.getLinkId());

        if(!linkOption.isPresent())
        {
            return  ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        var link = linkOption.get();
        var previousDocumentId = link.getDocumentId();
        var previousDownload = link.getDownload();
        var previousExpireAt = link.getExpiredAt();
        if(!permissionService.getOfLink(link,command).canWrite())
        {
            return  ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        if(link.getDocumentId() == null && !dataRoomLimitationService.isAllow(command.getCompanyId())){
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded,
                    Map.of());
        }

        var linkEntity = LinkMapper.INSTANCE.updateLinkEntity(link, command);

        var clearAuthorizedSession = false;
        if(command.getSecure()!=null && !command.getSecure().isEmpty())
        {
            try {

                var newSecureValue = command.getSecure();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.readValue(newSecureValue, SecureValue.class);
                linkEntity.setSecure(command.getSecure());
                clearAuthorizedSession =true;
            }catch(Exception ex)
            {
                logger.error(ex.getMessage(), ex);
                return ResponseMessageOf.ofBadRequest("Invalid request",
                        Map.of(UpdateLinkSetting.Fields.secure,"The secure setting is invalid."));
            }
        }

        if(link.getDocumentId() != null &&
                (!link.getDownload().equals(previousDownload) ||
                        (link.getExpiredAt() != null && !link.getExpiredAt().equals(previousExpireAt)))
        ) {
            var registerDocumentCommand = RegisterDocument.builder()
                    .fileId(link.getRefId())
                    .download(link.getDownload())
                    .expiredAt(link.getExpiredAt())
                    .build();

            registerDocumentCommand.setAccountId(command.getAccountId());

            var resultRegister = registerDocumentCommand.execute(pipeline);

            if (!resultRegister.getSucceeded()) {
                return ResponseMessageOf.ofBadRequest(resultRegister.getMessage(), Map.of());
            }

            link.setDocumentId(resultRegister.getData());

            try {
                var deleteDocument = new DeleteDocument(previousDocumentId);
                deleteDocument.execute(pipeline);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        linkEntityRepository.saveAndFlush(link);

        if(clearAuthorizedSession)
        {
            linkStatisticRepository.updateAuthorizedAtValue(link.getId());
        }

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);

    }

}
