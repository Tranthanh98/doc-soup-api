package logixtek.docsoup.api.features.dataroom.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor(staticName = "of")
public class ExportViewerOfDataRoom extends BaseIdentityCommand<ResponseEntity<byte[]>> {
    @Getter
    @Setter
    private Long dataRoomId;

    @Getter
    @Setter
    private Boolean email;
}
