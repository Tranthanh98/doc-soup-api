package logixtek.docsoup.api.features.link.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UpdateLinkStatus extends BaseIdentityCommand<ResponseEntity<String>> {
    UUID linkId;

    Boolean disabled;
}
