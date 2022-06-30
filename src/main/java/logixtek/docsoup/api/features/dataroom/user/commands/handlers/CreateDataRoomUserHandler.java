package logixtek.docsoup.api.features.dataroom.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.user.commands.CreateDataRoomUser;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DataRoomUserEntity;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomUserRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CreateDataRoomUser")
@AllArgsConstructor
public class CreateDataRoomUserHandler implements Command.Handler<CreateDataRoomUser, ResponseMessageOf<Long>> {
    private final DataRoomRepository dataRoomRepository;
    private final DataRoomUserRepository dataRoomUserRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseMessageOf<Long> handle(CreateDataRoomUser command) {
        if (!dataRoomLimitationService.isAllow(command.getCompanyId())) {
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded,
                    Map.of());
        }

        var dataRoomOption = dataRoomRepository.findById(command.getDataRoomId());
        if (!dataRoomOption.isPresent()) {
            return ResponseMessageOf.ofBadRequest("data room doesn't exist",
                    Map.of("id", "data room doesn't exist"));
        }

        if (!permissionService.getOfDataRoom(dataRoomOption.get(), command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var userOption = companyUserRepository.findFirstByAccountIdAndCompanyId(command.getUserId(), dataRoomOption.get().getCompanyId());

        if (!userOption.isPresent()) {
            return ResponseMessageOf.ofBadRequest("The user doesn't exist",
                    Map.of("id", "The user doesn't exist"));
        }

        var dataRoomUserOption = dataRoomUserRepository.findByUserIdAndDataRoomId(command.getUserId(), command.getDataRoomId());

        if (dataRoomUserOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.CREATED, dataRoomUserOption.get().getId());
        }

        var entity = new DataRoomUserEntity();
        entity.setDataRoomId(command.getDataRoomId());
        entity.setUserId(command.getUserId());
        entity.setCreatedBy(command.getAccountId());

        var dataRoomUser = dataRoomUserRepository.saveAndFlush(entity);

        if (dataRoomUser.getId() > 0) {
            return ResponseMessageOf.of(HttpStatus.CREATED, dataRoomUser.getId());
        }

        return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
    }
}