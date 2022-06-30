package logixtek.docsoup.api.features.dataroom.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.commands.ChangeOrderNoOfContent;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ChangeOrderNoOfContentHandler")
@AllArgsConstructor
public class ChangeOrderNoOfContentHandler implements Command.Handler<ChangeOrderNoOfContent, ResponseMessageOf<Long>> {

    private final DataRoomRepository dataRoomRepository;
    private final DataRoomContentRepository dataRoomContentRepository;
    private final PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseMessageOf<Long> handle(ChangeOrderNoOfContent command) {

        if (!dataRoomLimitationService.isAllow(command.getCompanyId())) {
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded,
                    Map.of());
        }

        if (command.getAfter() == null && command.getBefore() == null) {
            return ResponseMessageOf
                    .ofBadRequest("contentId and beforeId are not equal to null at the same time", Map.of());
        }

        var dataRoomOption = dataRoomRepository.findById(command.getId());

        if (!dataRoomOption.isPresent()) {
            return new ResponseMessageOf<>(HttpStatus.NOT_FOUND, "Not found the data room", Map.of());
        }

        var dataRoom = dataRoomOption.get();

        if (!permissionService.getOfDataRoom(dataRoom, command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN, dataRoom.getId());
        }

        var contentOption = dataRoomContentRepository.findById(command.getContentId());

        if (!contentOption.isPresent()) {
            return new ResponseMessageOf<>(HttpStatus.NOT_FOUND, "Not found the content", Map.of());
        }

        var content = contentOption.get();

        //move on top
        if (command.getBefore() == null) {
            var afterContentOption = dataRoomContentRepository
                    .findById(command.getAfter());

            if (!afterContentOption.isPresent()) {
                return ResponseMessageOf.of(HttpStatus.NO_CONTENT);
            }

            var afterContent = afterContentOption.get();

            if (afterContent.getOrderNo() != 1) {
                return ResponseMessageOf.ofBadRequest("invalid afterId", Map.of());
            }

            dataRoomContentRepository.updateIncreaseOrderNo(content.getId(), afterContent.getId());

            return ResponseMessageOf.of(HttpStatus.ACCEPTED, content.getId());
        }

        // move on bottom
        if (command.getAfter() == null) {
            var beforeContentOption = dataRoomContentRepository
                    .findById(command.getBefore());

            if (!beforeContentOption.isPresent()) {
                return ResponseMessageOf.of(HttpStatus.NO_CONTENT);
            }

            var beforeContent = beforeContentOption.get();


            var lastContent = dataRoomContentRepository
                    .findFirstByDataRoomIdOrderByOrderNoDesc(command.getId());

            if (lastContent.isPresent() && beforeContent.getOrderNo().equals(lastContent.get().getOrderNo())) {
                dataRoomContentRepository.updateDecreaseOrderNo(content.getId(), beforeContent.getId());

                return ResponseMessageOf.of(HttpStatus.ACCEPTED, content.getId());
            }
            return ResponseMessageOf.ofBadRequest("invalid beforeId", Map.of());
        }

        var afterContentOption = dataRoomContentRepository
                .findById(command.getAfter());

        var beforeContentOption = dataRoomContentRepository
                .findById(command.getBefore());

        if (afterContentOption.isPresent() && beforeContentOption.isPresent()) {

            var afterContent = afterContentOption.get();
            var beforeContent = beforeContentOption.get();

            if (afterContent.getOrderNo() - content.getOrderNo() == 1 &&
                    content.getOrderNo() - beforeContent.getOrderNo() == 1
            ) {
                return ResponseMessageOf.of(HttpStatus.ACCEPTED, content.getId());
            }

            if (afterContent.getOrderNo() - beforeContent.getOrderNo() != 1) {
                return ResponseMessageOf.ofBadRequest("beforeId and afterId are invalid", Map.of());
            }

            //drag item from top to bottom
            if (content.getOrderNo() < beforeContent.getOrderNo()) {
                dataRoomContentRepository.updateDecreaseOrderNo(content.getId(), beforeContent.getId());
                return ResponseMessageOf.of(HttpStatus.ACCEPTED);
            }

            //drag item from bottom to top
            if (content.getOrderNo() > afterContent.getOrderNo()) {
                dataRoomContentRepository.updateIncreaseOrderNo(content.getId(), afterContent.getId());
                return ResponseMessageOf.of(HttpStatus.ACCEPTED);
            }
        }

        return ResponseMessageOf.ofBadRequest("not found after content or before content", Map.of());
    }
}
