package logixtek.docsoup.api.features.contact.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.ContactWithLinkModel;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Data
public class ListAllContact extends BaseIdentityCommand<ResponseEntity<Collection<ContactWithLinkModel>>> {
    Boolean includeArchived = false;

    @Length(min = 3)
    String mode;
}
