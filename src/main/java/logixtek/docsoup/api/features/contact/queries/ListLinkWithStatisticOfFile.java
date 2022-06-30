package logixtek.docsoup.api.features.contact.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.PageStatisticOnContact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class ListLinkWithStatisticOfFile extends BaseIdentityCommand<Collection<PageStatisticOnContact>> {
    Long id;
    Long fileId;
}
