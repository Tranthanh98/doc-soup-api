package logixtek.docsoup.api.features.dataroom.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;

@Data
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
public class ChangeOrderNoOfContent extends BaseIdentityCommand<ResponseMessageOf<Long>> {
    @Min(1)
    Long id;

    @Min(1)
    Long contentId;

    @Min(1)
    Long before;

    @Min(1)
    Long after;
}
