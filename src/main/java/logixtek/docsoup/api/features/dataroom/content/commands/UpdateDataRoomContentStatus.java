package logixtek.docsoup.api.features.dataroom.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class UpdateDataRoomContentStatus extends BaseIdentityCommand<ResponseMessageOf<Long>> {
    @Min(1)
    private Long dataRoomId;

    @Min(1)
    private Long contentId;

    private Boolean isActive = true;
}
