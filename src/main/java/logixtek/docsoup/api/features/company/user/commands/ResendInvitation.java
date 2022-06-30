package logixtek.docsoup.api.features.company.user.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;

@Data
public class ResendInvitation extends BaseIdentityCommand<ResponseMessageOf<Long>> {
    Long targetCompanyUserId;
}
