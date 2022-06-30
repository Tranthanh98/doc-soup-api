package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.responses.LinkDirectory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class GetLinkDirectory implements Command<ResponseEntity<LinkDirectory>> {
    UUID linkId;
    Long contentId;
    Long directoryId;
    String deviceId;
    Long viewerId ;
}
