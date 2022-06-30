package logixtek.docsoup.api.features.share.document.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper=true)
public class RegisterDocument extends BaseIdentityCommand<ResultOf<UUID>> {

    @Min(1)
    Long fileId;

    Boolean download ;

    OffsetDateTime expiredAt;
}
