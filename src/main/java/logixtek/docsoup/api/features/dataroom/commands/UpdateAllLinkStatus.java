package logixtek.docsoup.api.features.dataroom.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UpdateAllLinkStatus extends BaseIdentityCommand<ResponseMessageOf<String>> {
    @Min(0)
    Long id;

    Boolean disabled = false;
}
