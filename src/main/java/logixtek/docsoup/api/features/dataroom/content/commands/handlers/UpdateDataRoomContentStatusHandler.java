package logixtek.docsoup.api.features.dataroom.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.content.commands.UpdateDataRoomContentStatus;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("UpdateDataRoomContentStatusHandler")
@AllArgsConstructor
public class UpdateDataRoomContentStatusHandler implements Command.Handler<UpdateDataRoomContentStatus, ResponseMessageOf<Long>> {

    private final DataRoomContentRepository dataRoomContentRepository;
    private final DataRoomRepository dataRoomRepository;
    private final PermissionService permissionService;

    @Override
    public ResponseMessageOf<Long> handle(UpdateDataRoomContentStatus command) {
        var dataRoomOption = dataRoomRepository.findById(command.getDataRoomId());

        if (!dataRoomOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        if (!permissionService.getOfDataRoom(dataRoomOption.get(), command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }
        var dataRoomContentOption = dataRoomContentRepository.findById(command.getContentId());

        if (!dataRoomContentOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }
        var dataRoomContent = dataRoomContentOption.get();

        if (dataRoomContent.getIsActive().equals(command.getIsActive())) {
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        dataRoomContent.setIsActive(command.getIsActive());
        dataRoomContentRepository.saveAndFlush(dataRoomContent);

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
