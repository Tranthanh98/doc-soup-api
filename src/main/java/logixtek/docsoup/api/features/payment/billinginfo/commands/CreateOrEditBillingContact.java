package logixtek.docsoup.api.features.payment.billinginfo.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor(staticName = "of")
public class CreateOrEditBillingContact extends BaseIdentityCommand<ResponseEntity<String>> {
    @NotBlank
    @Length(max = 150)
    private String email;
}
