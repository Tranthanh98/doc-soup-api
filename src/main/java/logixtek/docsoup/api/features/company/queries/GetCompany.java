package logixtek.docsoup.api.features.company.queries;

import logixtek.docsoup.api.features.company.responses.CompanyInfoWithPlanTier;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetCompany extends BaseIdentityCommand<ResponseMessageOf<CompanyInfoWithPlanTier>> {
    UUID id;
}
