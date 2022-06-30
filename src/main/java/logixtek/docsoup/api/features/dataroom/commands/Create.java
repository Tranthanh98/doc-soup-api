package logixtek.docsoup.api.features.dataroom.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class Create extends BaseIdentityCommand<ResponseMessageOf<Long>> {

    @NotNull(message = "name is mandatory")
    private String name;
}
