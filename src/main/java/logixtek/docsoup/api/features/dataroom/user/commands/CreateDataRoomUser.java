package logixtek.docsoup.api.features.dataroom.user.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class CreateDataRoomUser extends BaseIdentityCommand<ResponseMessageOf<Long>> {
    @Min(1)
    Long dataRoomId;

    @NotBlank
    @Size(min = 36, max = 36)
    String userId;
}