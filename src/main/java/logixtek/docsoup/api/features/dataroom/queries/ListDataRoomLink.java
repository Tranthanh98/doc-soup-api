package logixtek.docsoup.api.features.dataroom.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.LinkInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListDataRoomLink extends BaseIdentityCommand<ResponseEntity<Collection<LinkInformation>>> {
    Long dataRoomId;
}
