package logixtek.docsoup.api.features.dataroom.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.queries.ListAll;
import logixtek.docsoup.api.infrastructure.constants.DataRoomConstant;
import logixtek.docsoup.api.infrastructure.models.DataRoomInfo;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("ListAllDataRoomHandler")
@AllArgsConstructor
public class ListAllHandler implements Command.Handler<ListAll, ResponseEntity<List<DataRoomInfo>>> {

    private final DataRoomRepository dataRoomRepository;

    @Override
    public ResponseEntity<List<DataRoomInfo>> handle(ListAll listAll) {
        var result = new ArrayList<DataRoomInfo>();

        if(listAll.getFilter().equals(DataRoomConstant.MINE)){
            var dataRooms = dataRoomRepository
                    .findAllDataRoomsByAccountIdAndCompanyId(listAll.getAccountId(), listAll.getCompanyId().toString());

            if (dataRooms.isPresent()) {
                result.addAll(dataRooms.get());
            }
        }

        else if (listAll.getFilter().equals(DataRoomConstant.COLLABORATE)) {
            var invitedDataRooms = dataRoomRepository
                    .findAllInvitedDataRoomsByUserIdAndCompanyId(listAll.getAccountId(), listAll.getCompanyId().toString());

            if (invitedDataRooms.isPresent() && !invitedDataRooms.isEmpty()) {
                result.addAll(invitedDataRooms.get());
            }
        }

        return ResponseEntity.ok(result);
    }
}
