package logixtek.docsoup.api.features.company.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.CompanyOfUser;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;

@Data
public class GetActiveCompany extends BaseIdentityCommand<ResponseMessageOf<CompanyOfUser>> {
}
