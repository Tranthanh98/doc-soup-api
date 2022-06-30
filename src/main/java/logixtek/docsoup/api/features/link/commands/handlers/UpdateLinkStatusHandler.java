package logixtek.docsoup.api.features.link.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.commands.UpdateLinkStatus;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UpdateLinkStatusHandler")
@AllArgsConstructor
public class UpdateLinkStatusHandler implements Command.Handler<UpdateLinkStatus, ResponseEntity<String>> {
    private final LinkRepository linkEntityRepository;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseEntity<String> handle(UpdateLinkStatus command) {

        var linkEntityOption = linkEntityRepository.findById(command.getLinkId());

        if(!linkEntityOption.isPresent()) {
           return  ResponseEntity.notFound().build();
        }

        var linkEntity = linkEntityOption.get();

        if(linkEntity.getDocumentId() == null
                && !dataRoomLimitationService.isAllow(command.getCompanyId())
                && command.getDisabled().equals(false)
        ){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ResponseResource.LimitedPlanExceeded);
        }

        if(!linkEntity.getCompanyId().toString().equals(command.getCompanyId().toString())
                || !linkEntity.getCreatedBy().equals(command.getAccountId()))
        {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var linkStatus = Boolean.TRUE.equals(command.getDisabled()) ? LinkConstant.DISABLED_STATUS : LinkConstant.ACTIVE_STATUS;
        linkEntity.setStatus(linkStatus);

        linkEntityRepository.saveAndFlush(linkEntity);

        return ResponseEntity.accepted().build();
    }
}
