package logixtek.docsoup.api.features.company.user.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AddBatchCompanyUser extends BaseIdentityCommand<ResponseMessageOf<List<Long>>> {

    @NotNull
    List<@Email @NotBlank String> emails;

    @NotBlank
    @Size(min = 7, max = 50)
    private String role;
}
