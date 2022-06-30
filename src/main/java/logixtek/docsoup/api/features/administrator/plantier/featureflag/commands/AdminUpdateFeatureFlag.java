package logixtek.docsoup.api.features.administrator.plantier.featureflag.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;

@Data
public class AdminUpdateFeatureFlag extends BaseAdminIdentityCommand<ResponseMessageOf<String>> {

    Integer limit;

    Long featureFlagId;

}