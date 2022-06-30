package logixtek.docsoup.api.features.company.team.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.team.queries.ListContentUser;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.models.UserStatisticOfFile;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@Component("ListContentUserHandler")
public class ListContentUserHandler implements Command.Handler<ListContentUser, ResponseEntity<Collection<UserStatisticOfFile>>> {

    private final CompanyUserCacheService companyUserCacheService;
    private final FileRepository fileRepository;

    @Override
    public ResponseEntity<Collection<UserStatisticOfFile>> handle(ListContentUser query) {

        var companyUser = companyUserCacheService.get(query.getAccountId(),
                query.getCompanyId());
        if(companyUser ==null || !companyUser.getRole().equals(RoleDefinition.C_ADMIN)){
            return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var fileContentWithDataRoom = fileRepository.getListFileWithLinksAndDataRoomsByAccountId(
                query.getUserId(),
                query.getCompanyId().toString(),
                query.getNumOfRecentDay()
        );

        if(fileContentWithDataRoom.isEmpty() || fileContentWithDataRoom.get().isEmpty()){
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(fileContentWithDataRoom.get());
    }
}
