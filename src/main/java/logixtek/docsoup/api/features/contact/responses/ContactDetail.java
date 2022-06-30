package logixtek.docsoup.api.features.contact.responses;

import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ContactDetail {

    private ContactEntity contact;

    private List<String>  linkNames;

    private Boolean signedNDA = false;
}
