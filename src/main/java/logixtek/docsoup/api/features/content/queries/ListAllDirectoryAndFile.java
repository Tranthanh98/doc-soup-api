package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.features.content.responses.AggregateContentViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.*;

import javax.validation.constraints.Min;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ListAllDirectoryAndFile extends BaseIdentityCommand<ResponseMessageOf<List<AggregateContentViewModel>>> {

    @NonNull
    @Min(1)
    Long directoryId;
}
