package logixtek.docsoup.api.features.company.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UpdateCompany extends BaseIdentityCommand<ResponseMessageOf<String>> {
    UUID id;
    @NotBlank
    @NotNull
    String name;

    Boolean trackingOwnerVisit = true;
}
