package logixtek.docsoup.api.features.company.user.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TransferData extends BaseIdentityCommand<ResponseMessageOf<String>> {
    String sourceAccountId;

    @NotNull
    @NotBlank
    @Length(min = 36, max = 36)
    String destinationAccountId;
}
