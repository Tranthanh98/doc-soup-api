package logixtek.docsoup.api.features.dataroom.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.commands.UpdateAllLinkStatus;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessage;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UpdateAllLinkStatusHandler")
@AllArgsConstructor
public class UpdateAllLinkStatusHandler implements Command.Handler<UpdateAllLinkStatus, ResponseMessageOf<String>> {

    private final DataRoomRepository dataRoomRepository;
    private final PermissionService permissionService;
    private final LinkRepository linkRepository;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseMessageOf<String> handle(UpdateAllLinkStatus command) {

        if (command.getDisabled().equals(false) && Boolean.TRUE.equals(!dataRoomLimitationService.isAllow(command.getCompanyId()))) {
            return ResponseMessageOf.of(HttpStatus.UNPROCESSABLE_ENTITY,
                    ResponseResource.LimitedPlanExceeded, Map.of(), ResponseResource.LimitedPlanExceeded);
        }

        var dataRoomOption = dataRoomRepository.findById(command.getId());

        if (dataRoomOption.isEmpty()) {
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        var dataRoom = dataRoomOption.get();

        if (!permissionService.get(dataRoom, command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        linkRepository.updateAllDataRoomLinkStatus(command.getId(), command.getDisabled());

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
