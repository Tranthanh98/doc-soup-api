package logixtek.docsoup.api.features.link.queries;

import logixtek.docsoup.api.features.link.responses.LinkSettingViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@FieldNameConstants
public class GetLinkSetting extends BaseIdentityCommand<ResponseMessageOf<LinkSettingViewModel>> {
    UUID linkId;
}