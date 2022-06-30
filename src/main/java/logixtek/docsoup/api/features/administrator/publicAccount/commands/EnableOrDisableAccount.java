package logixtek.docsoup.api.features.administrator.publicAccount.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;

@Data
public class EnableOrDisableAccount extends BaseAdminIdentityCommand<ResponseMessageOf<String>> {

    String accountId;

    boolean enable;

}
