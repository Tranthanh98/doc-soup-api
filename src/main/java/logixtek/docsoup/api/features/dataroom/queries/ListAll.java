package logixtek.docsoup.api.features.dataroom.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.DataRoomInfo;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Data
public class ListAll extends BaseIdentityCommand<ResponseEntity<List<DataRoomInfo>>> {
    String filter;
}