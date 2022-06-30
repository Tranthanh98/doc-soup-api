package logixtek.docsoup.api.features.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@FieldNameConstants
public class RenameDirectory extends BaseIdentityCommand<ResponseMessageOf<String>> {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    @NotBlank(message = "newName is mandatory")
    @Size(min=3, max=200,message = "Length must be greater than 3 and less than 200")
    private String newName;

}
