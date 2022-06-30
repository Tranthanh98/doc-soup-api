package logixtek.docsoup.api.features.link.linkAccount.responses;

import logixtek.docsoup.api.infrastructure.models.LinkAccountWithActivityInfor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor(staticName = "of")
public class LinkAccountViewModel {
    Integer totalLinkAccount;
    Collection<LinkAccountWithActivityInfor> items;
}
