package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.models.LinkInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetSubLink implements Command<ResponseEntity<UUID>> {
    LinkInformation parentLink;
    FileEntity file;
}
