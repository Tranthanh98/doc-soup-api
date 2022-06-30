package logixtek.docsoup.api.features.administrator.plantier.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;

@Data
public class AdminUpdatePlanTier extends BaseAdminIdentityCommand<ResponseMessageOf<String>> {

    Long id;

    String name;

    String description;

}
