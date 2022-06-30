package logixtek.docsoup.api.features.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;

@NoArgsConstructor
@FieldNameConstants
public class MoveFile extends BaseIdentityCommand<ResponseMessageOf<String>> {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    @Min(1)
    private long newDirectoryId;

}