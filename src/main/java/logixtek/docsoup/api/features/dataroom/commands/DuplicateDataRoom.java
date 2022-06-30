package logixtek.docsoup.api.features.dataroom.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class DuplicateDataRoom extends BaseIdentityCommand<ResponseMessageOf<Long>> {
    @Min(1)
    Long id;

    @Size(min = 1, max = 255)
    String name;
}
