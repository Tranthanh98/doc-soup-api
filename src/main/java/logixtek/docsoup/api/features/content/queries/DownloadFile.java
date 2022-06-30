package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public class DownloadFile extends BaseIdentityCommand<ResponseEntity<Resource>> {

    public static DownloadFile of(Long id)
    {
        var instance = new DownloadFile();
        instance.setId(id);
        return  instance;
    }

    @Getter
    @Setter
    Long id;
}

