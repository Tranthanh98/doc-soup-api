package logixtek.docsoup.api.features.dataroom.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.content.commands.AddContent;
import logixtek.docsoup.api.features.dataroom.content.mappers.DataRoomContentMapper;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("AddContentDataRoomHandler")
@AllArgsConstructor
public class AddContentHandler implements Command.Handler<AddContent, ResponseMessageOf<List<Long>>> {

    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;
    private final DataRoomRepository dataRoomRepository;
    private final DataRoomContentRepository dataRoomContentRepository;
    private final PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseMessageOf<List<Long>> handle(AddContent command) {
        if (!dataRoomLimitationService.isAllow(command.getCompanyId(), command.getId())) {
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded, Map.of());
        }

        var isExistDataRoom = dataRoomRepository.findByIdAndAccountIdAndCompanyId(command.getId(),
                command.getAccountId(), command.getCompanyId());

        if (!isExistDataRoom.isPresent()) {
            return new ResponseMessageOf<List<Long>>(HttpStatus.BAD_REQUEST);
        }

        if (!permissionService.getOfDataRoom(isExistDataRoom.get(), command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        Map<String, String> fieldErrors = new HashMap<>();

        if (command.getDirectoryIds() != null) {
            var addDirectoryErrors = this.addDirectories(command);

            if (!addDirectoryErrors.isEmpty()) {
                fieldErrors.putAll(addDirectoryErrors);
            }
        }

        if (command.getFileIds() != null) {
            var addFileErrors = this.addFiles(command);

            if (!addFileErrors.isEmpty()) {
                fieldErrors.putAll(addFileErrors);
            }
        }

        if (!fieldErrors.isEmpty()) {
            return ResponseMessageOf.ofBadRequest("invalid directoryId or fileId", fieldErrors);
        }

        return new ResponseMessageOf<>(HttpStatus.ACCEPTED);
    }

    private Map<String, String> addDirectories(AddContent command) {
        Map<String, String> fieldErrors = new HashMap<>();

        List<DataRoomContentEntity> dataRoomContentEntities = new ArrayList<>();

        var maxOrderNo = dataRoomContentRepository.getMaxOrderNoByDataRoomId(command.getId());

        var nextOrderNo = maxOrderNo != null ? maxOrderNo.intValue() + 1 : 1;

        for (Long directoryId : command.getDirectoryIds()) {
            var directory = directoryRepository.findById(directoryId);

            if (!directory.isPresent() || permissionService.get(directory.get(), command).isDenied()) {
                fieldErrors.put(directoryId.toString(), "directoryId doesn't exist");

            } else {

                var directoryOfRoom =
                        dataRoomContentRepository.findByDataRoomIdAndDirectoryId(command.getId(), directoryId);

                if (!directoryOfRoom.isPresent()) {
                    var dataRoomContentEntity = DataRoomContentMapper.INSTANCE.toEntity(command);
                    dataRoomContentEntity.setDirectoryId(directoryId);
                    dataRoomContentEntity.setOrderNo(nextOrderNo);

                    dataRoomContentEntities.add(dataRoomContentEntity);

                    nextOrderNo++;
                }

            }
        }

        dataRoomContentRepository.saveAllAndFlush(dataRoomContentEntities);

        return fieldErrors;
    }

    private Map<String, String> addFiles(AddContent command) {
        Map<String, String> fieldErrors = new HashMap<>();

        List<DataRoomContentEntity> dataRoomContentEntities = new ArrayList<>();

        var maxOrderNo = dataRoomContentRepository.getMaxOrderNoByDataRoomId(command.getId());

        var nextOrderNo = maxOrderNo != null ? maxOrderNo.intValue() + 1 : 1;

        for (Long fileId : command.getFileIds()) {
            var file = fileRepository.findById(fileId);

            if (!file.isPresent() || permissionService.getOfFile(file.get(), command).isDenied()) {
                fieldErrors.put(fileId.toString(), "fileId doesn't exist");

            } else {
                var fileOfRoom =
                        dataRoomContentRepository.findByDataRoomIdAndFileId(command.getId(), fileId);

                if (!fileOfRoom.isPresent()) {
                    var dataRoomContentEntity = DataRoomContentMapper.INSTANCE.toEntity(command);
                    dataRoomContentEntity.setFileId(fileId);
                    dataRoomContentEntity.setOrderNo(nextOrderNo);
                    dataRoomContentEntities.add(dataRoomContentEntity);

                    nextOrderNo++;
                }
            }
        }

        dataRoomContentRepository.saveAllAndFlush(dataRoomContentEntities);

        return fieldErrors;
    }
}
