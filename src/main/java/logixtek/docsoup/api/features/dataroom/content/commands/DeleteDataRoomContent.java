package logixtek.docsoup.api.features.dataroom.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class DeleteDataRoomContent extends BaseIdentityCommand<ResponseMessageOf<Long>> {
    @Min(1)
    private Long dataRoomId;

    @Min(1)
    private Long contentId;
}