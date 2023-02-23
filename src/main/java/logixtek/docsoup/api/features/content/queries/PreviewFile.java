package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;

public class PreviewFile extends BaseIdentityCommand<ResponseEntity<InputStreamResource>> {

    public PreviewFile(long fileId)
    {
        id = fileId;
    }

    @Getter
    @Min(1)
    private final Long id;

}
