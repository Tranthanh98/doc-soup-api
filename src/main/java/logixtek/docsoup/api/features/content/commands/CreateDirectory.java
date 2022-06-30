package logixtek.docsoup.api.features.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@FieldNameConstants
public class CreateDirectory extends BaseIdentityCommand<ResponseMessageOf<Long>> {

    @Getter
    @Setter
    private long parentId = 0;

    @Getter
    @Setter
    @NotBlank(message = "Name is mandatory")
    @NotNull(message = "Name is mandatory")
    @Size(min=3, max=200,message = "Length must be greater than 3 and less than 200")
    private String name;

    @Getter
    @Setter
    @NotNull(message = "IsTeam is mandatory")
    Boolean isTeam;

}
