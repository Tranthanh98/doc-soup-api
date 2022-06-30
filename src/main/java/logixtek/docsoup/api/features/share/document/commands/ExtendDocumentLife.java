package logixtek.docsoup.api.features.share.document.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExtendDocumentLife extends BaseIdentityCommand<Result> {

    @NonNull
    Long fileId;

    @NonNull
    OffsetDateTime expiredAt;
}
