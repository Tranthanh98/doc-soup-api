package logixtek.docsoup.api.features.dataroom.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.commands.Create;
import logixtek.docsoup.api.features.dataroom.mappers.DataRoomMapper;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CreateDataRoomHandler")
@AllArgsConstructor
public class CreateHandler implements Command.Handler<Create, ResponseMessageOf<Long>> {

    private final DataRoomRepository dataRoomRepository;

    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseMessageOf<Long> handle(Create create) {

        if (!dataRoomLimitationService.isAllow(create.getCompanyId())) {
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded,
                    Map.of());
        }

        var entity = DataRoomMapper.INSTANCE.toEntity(create);

        var dataRoom = dataRoomRepository.saveAndFlush(entity);

        if (dataRoom.getId() > 0) {
            return ResponseMessageOf.of(HttpStatus.CREATED, dataRoom.getId());
        }

        return new ResponseMessageOf<>(HttpStatus.BAD_REQUEST);
    }
}
