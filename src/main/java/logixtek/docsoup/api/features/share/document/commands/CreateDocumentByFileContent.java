package logixtek.docsoup.api.features.share.document.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class CreateDocumentByFileContent extends BaseIdentityCommand<ResultOf<DocumentEntity>> {
    @NotNull
    private byte[] fileContent;

    private String fileName;

    private Boolean download;

    private Boolean save;

    private String lifeSpan;

    private Boolean print;

    private OffsetDateTime expiredAt;
}
