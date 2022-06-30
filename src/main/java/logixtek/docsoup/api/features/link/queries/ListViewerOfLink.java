package logixtek.docsoup.api.features.link.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.Viewer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ListViewerOfLink extends BaseIdentityCommand<Collection<Viewer>> {
    UUID linkId;
}
