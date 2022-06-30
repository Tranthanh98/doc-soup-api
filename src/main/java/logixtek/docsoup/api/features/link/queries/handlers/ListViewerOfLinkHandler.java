package logixtek.docsoup.api.features.link.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.queries.ListViewerOfLink;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.Viewer;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component("ListViewerOfLinkHandler")
@AllArgsConstructor
public class ListViewerOfLinkHandler implements Command.Handler<ListViewerOfLink, Collection<Viewer>> {

    private  final ContactRepository contactRepository;

    private final LinkRepository linkRepository;

    private  final PermissionService permissionService;


    @Override
    public Collection<Viewer> handle(ListViewerOfLink query) {

        var linkOption = linkRepository.findById(query.getLinkId());

        if(linkOption.isPresent() && permissionService.getOfLink(linkOption.get(),query).canRead()) {
            var result = new ArrayList<Viewer>();
            var viewerOption = contactRepository.findAllViewerByLinkId(query.getLinkId());

            if (viewerOption.isPresent()) {
                result.addAll(viewerOption.get());
            }

            return result;
        }

        return Collections.emptyList();

    }
}
