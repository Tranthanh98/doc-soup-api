package logixtek.docsoup.api.features.company.user.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CancelInvitation extends BaseIdentityCommand<ResponseMessageOf<String>> {
    Long targetCompanyUserId;
}
