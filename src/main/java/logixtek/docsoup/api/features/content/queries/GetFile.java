package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;

public class GetFile extends BaseIdentityCommand<ResponseEntity<FileEntity>> {

    public GetFile(long fileId)
    {
        id = fileId;
    }

    @Getter
    @Min(1)
    private final Long id;

}
