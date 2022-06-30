package logixtek.docsoup.api.features.administrator.plantier.queries;

import logixtek.docsoup.api.features.administrator.plantier.responses.AdminPlanTierDetail;
import logixtek.docsoup.api.features.administrator.plantier.responses.AdminPlanTierViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor(staticName = "of")
public class GetAdminPlanTier extends BaseAdminIdentityCommand<ResponseEntity<AdminPlanTierDetail>> {

    Long planTierId;

}
