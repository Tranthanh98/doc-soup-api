package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

public class ExportViewerOfFile extends BaseIdentityCommand<ResponseEntity<byte[]>> {
    public static ExportViewerOfFile of(Long id, Boolean email)
    {
        var instance = new ExportViewerOfFile();
        instance.setFileId(id);
        instance.setEmail(email);
        return instance;
    }

    @Getter
    @Setter
    private Long fileId;

    @Getter
    @Setter
    private Boolean email;
}
