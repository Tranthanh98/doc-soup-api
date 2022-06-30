package logixtek.docsoup.api.features.share.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
public class DeleteFile  extends BaseIdentityCommand<ResponseEntity<String>> {
    @Min(1)
    private long id;

    Boolean nda = false;
}
