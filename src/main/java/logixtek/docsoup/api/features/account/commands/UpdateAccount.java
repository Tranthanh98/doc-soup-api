package logixtek.docsoup.api.features.account.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class UpdateAccount extends BaseIdentityCommand<ResponseMessageOf<String>> {
    @NotBlank
    String firstName;

    String phone;
}
