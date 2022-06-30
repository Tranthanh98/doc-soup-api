package logixtek.docsoup.api.features.link.linkAccount.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.DataRoomInfo;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Collection;

@Data
@AllArgsConstructor(staticName = "of")
public class GetListDataRoomOfLinkAccount extends BaseIdentityCommand<ResponseMessageOf<Collection<DataRoomInfo>>> {
    @Min(1)
    Long linkAccountId;
}
