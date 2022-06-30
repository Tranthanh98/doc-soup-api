package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.FileEntityWithVisits;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;
import java.util.List;

public class ListAllFile extends BaseIdentityCommand<ResponseEntity<List<FileEntityWithVisits>>> {

    public  ListAllFile(long dirId)
    {
        directoryId=dirId;
    }

    @Getter
    @Min(1)
    private final long directoryId;

}

