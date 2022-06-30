package logixtek.docsoup.api.features.administrator.plantier.queries;

import logixtek.docsoup.api.features.administrator.plantier.responses.AdminPlanTierViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public class AdminListAllPlanTier extends BaseAdminIdentityCommand<ResponseEntity<Collection<AdminPlanTierViewModel>>> {
}
