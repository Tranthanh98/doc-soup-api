package logixtek.docsoup.api.features.contact.queries;

import logixtek.docsoup.api.features.contact.responses.ContactDetail;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetContact extends BaseIdentityCommand<ResponseEntity<ContactDetail>> {
    Long id;
}
