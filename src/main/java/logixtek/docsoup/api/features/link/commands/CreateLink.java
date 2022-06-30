package logixtek.docsoup.api.features.link.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper=true)
public class CreateLink extends BaseIdentityCommand<ResponseMessageOf<UUID>> {
    @Min(1)
    Long resourceId = Long.valueOf(0);

    Boolean download = false;

    @Range(min=0,max = 1)
    Integer linkType;

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
