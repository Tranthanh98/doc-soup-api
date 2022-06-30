package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.ListPageStatisticOfViewerOnFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.PageStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("ListPageStatisticOfViewerOnFileHandler")
@AllArgsConstructor
public class ListPageStatisticOfViewerOnFileHandler implements Command.Handler<ListPageStatisticOfViewerOnFile, Collection<PageStatistic>> {

    private  final PageStatisticRepository pageStatisticRepository;
    private  final FileRepository fileRepository;
    private  final PermissionService permissionService;

    @Override
    public Collection<PageStatistic> handle(ListPageStatisticOfViewerOnFile query) {

        var fileOption = fileRepository.findById(query.getFileId());

        if(fileOption.isPresent() && permissionService.getOfFile(fileOption.get(),query).canRead()) {

            var resultOption = pageStatisticRepository.findAllPageStatisticByLinkStatisticId(query.getViewerId());

            if (resultOption.isPresent()) {
                return resultOption.get();
            }
        }
        return Collections.emptyList();
    }
}
