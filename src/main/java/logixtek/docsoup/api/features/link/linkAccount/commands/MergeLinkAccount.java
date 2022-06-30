package logixtek.docsoup.api.features.link.linkAccount.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class MergeLinkAccount extends BaseIdentityCommand<ResponseMessageOf<String>> {
    @Min(1)
    Long sourceLinkAccountId;

    @Min(1)
    Long destinationLinkAccountId;
}
