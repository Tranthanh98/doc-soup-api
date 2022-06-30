package logixtek.docsoup.api.features.payment.billinginfo.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class CreateOrEditBillingInfo extends BaseIdentityCommand<ResponseEntity<String>> {
    @NotBlank
    @Length(max = 150)
    private String billingInfoName;

    @Length(max = 150)
    private String billingInfoStreet;

    @Length(max = 150)
    private String billingInfoCity;

    @Length(max = 150)
    private String billingInfoState;

    @Length(max = 25)
    private String billingInfoZipCode;

    @Length(max = 25)
    private String billingInfoTaxId;
}
