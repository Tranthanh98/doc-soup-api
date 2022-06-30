package logixtek.docsoup.api.features.dataroom.queries;

import logixtek.docsoup.api.features.dataroom.responses.DataRoomDetail;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor(staticName = "of")
public class Get extends BaseIdentityCommand<ResponseEntity<DataRoomDetail>> {
    @Getter
    @Setter
    private Long id;
}
