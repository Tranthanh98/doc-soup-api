package logixtek.docsoup.api.features.administrator.plantier.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class EnableOrDisablePlanTier extends BaseAdminIdentityCommand<ResponseMessageOf<String>> {

    Long planTierId;
    Boolean isActive = false;

}
