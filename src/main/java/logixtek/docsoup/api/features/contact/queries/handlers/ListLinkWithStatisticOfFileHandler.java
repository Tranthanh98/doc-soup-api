package logixtek.docsoup.api.features.contact.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.contact.queries.ListLinkWithStatisticOfFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.PageStatisticOnContact;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.PageStatisticRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("ListLinkWithStatisticOfFileHandler")
@AllArgsConstructor
@Getter
@Setter
public class ListLinkWithStatisticOfFileHandler implements Command.Handler<ListLinkWithStatisticOfFile, Collection<PageStatisticOnContact>>  {
    private final PageStatisticRepository pageStatisticRepository;
    private final FileRepository fileRepository;
    private final PermissionService permissionService;

    @Override
    public Collection<PageStatisticOnContact> handle(ListLinkWithStatisticOfFile query) {
        var fileOption = fileRepository.findById(query.getFileId());

        if(fileOption.isPresent() && permissionService.getOfFile(fileOption.get(),query).canRead()) {
            var pageOption = pageStatisticRepository.findAllPageStatisticByContactIdAndFileId(query.getId(), query.getFileId());

            if (pageOption.isPresent()) {
                return pageOption.get();
            }
        }

        return Collections.emptyList();
    }
}
