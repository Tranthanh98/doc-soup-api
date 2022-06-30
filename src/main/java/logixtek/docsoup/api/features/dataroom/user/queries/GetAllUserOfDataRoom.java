package logixtek.docsoup.api.features.dataroom.user.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.DataRoomUser;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor(staticName = "of")
public class GetAllUserOfDataRoom extends BaseIdentityCommand<ResponseMessageOf<List<DataRoomUser>>> {
    @Getter
    @Setter
    private Long dataRoomId;
}
