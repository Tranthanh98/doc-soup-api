package logixtek.docsoup.api.features.contact.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class UpdateContactStatus extends BaseIdentityCommand<ResponseEntity<String>> {

    @Getter
    @Setter
    Long contactId;

    @Getter
    @Setter
    Boolean archived;
}
