package logixtek.docsoup.api.features.account.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@FieldNameConstants
public class ResetPassword implements Command<ResponseMessageOf<String>> {
    @NotNull
    @NotBlank
    String token;

    @NotNull
    @NotBlank
    String password;

    @NotNull
    @NotBlank
    String confirmPassword;
}
