package logixtek.docsoup.api.features.link.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class UpdateLinkSetting extends BaseIdentityCommand<ResponseMessageOf<String>> {
    UUID linkId;

    Boolean download = false;

    @Nullable
    Long watermarkId;

    @Nullable
    String secure;

    @Nullable
    OffsetDateTime expiredAt;

    @Nullable
    Long ndaId;

    @NotNull
    @Min(1)
    Long linkAccountsId;
}
