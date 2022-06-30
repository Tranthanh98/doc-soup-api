package logixtek.docsoup.api.features.setting.nda.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;

public class PreviewNDA extends BaseIdentityCommand<ResponseEntity<String>> {

    @Getter
    @Setter
    @Min(1)
    Long id;
}
