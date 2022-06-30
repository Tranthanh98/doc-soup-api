package logixtek.docsoup.api.features.administrator.publicAccount.responses;

import logixtek.docsoup.api.infrastructure.models.PublicAccount;
import logixtek.docsoup.api.infrastructure.models.UserCompanyWithPlanTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Data
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class PublicAccountDetail {
    PublicAccount account;

    Collection<UserCompanyWithPlanTier> companies;
}
