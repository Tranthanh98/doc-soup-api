package logixtek.docsoup.api.features.account.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class ChangePassword extends BaseIdentityCommand<ResponseMessageOf<String>> {

    @NotBlank
    @NotEmpty
    @NotNull
    String currentPass;

    @NotBlank
    @NotEmpty
    @NotNull
    String newPass;

    @NotBlank
    @NotEmpty
    @NotNull
    String confirmNewPass;
}
