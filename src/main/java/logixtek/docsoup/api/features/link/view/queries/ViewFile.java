package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NonNull
public class ViewFile extends BaseViewFile implements Command<ResponseEntity<UUID>> {
    Long fileId;
}
