package logixtek.docsoup.api.features.dataroom.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class AddContent extends BaseIdentityCommand<ResponseMessageOf<List<Long>>> {

    @Min(1)
    private Long id;

    private List<Long> directoryIds;

    private List<Long> fileIds;
}
