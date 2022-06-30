package logixtek.docsoup.api.features.company.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper=true)
public class CreateCompany extends BaseIdentityCommand<ResponseEntity<String>> {
    @NotNull
    @NotBlank
    String companyName;

    String owner;
}
