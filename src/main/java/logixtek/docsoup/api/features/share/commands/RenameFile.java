package logixtek.docsoup.api.features.share.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
public class RenameFile extends BaseIdentityCommand<ResponseMessageOf<String>> {

    private long id;

    @NotBlank
    String newName;

    @NotNull
    Boolean nda;
}
