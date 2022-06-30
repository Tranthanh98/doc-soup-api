package logixtek.docsoup.api.features.account.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ForgotPassword extends BaseIdentityCommand<ResponseMessageOf<String>> {

}
