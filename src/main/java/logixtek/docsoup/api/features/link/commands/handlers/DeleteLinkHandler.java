package logixtek.docsoup.api.features.link.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.commands.DeleteLink;
import logixtek.docsoup.api.features.share.domainevents.DeleteLinkDomainEvent;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.repositories.PageStatisticRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("DeleteLinkHandler")
@AllArgsConstructor
public class DeleteLinkHandler implements Command.Handler<DeleteLink, ResponseMessageOf<String>> {

    private final LinkRepository linkRepository;
    private final LinkStatisticRepository linkStatisticRepository;
    private final PermissionService permissionService;
    private final PageStatisticRepository pageStatisticRepository;
    private final DocumentRepository documentRepository;
    private final Pipeline pipeline;

    @SneakyThrows
    @Override
    public ResponseMessageOf<String> handle(DeleteLink command) {
        var linkOption = linkRepository.findById(command.getLinkId());

        if(linkOption.isEmpty()){
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        var link = linkOption.get();

        if(!permissionService.getOfLink(link, command).canWrite()){
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        if(link.getDocumentId() != null){
            var documentOption = documentRepository.findById(link.getDocumentId());

            if(documentOption.isPresent()){
                var document = documentOption.get();
                // raise deleteLinkDomainEvent
                var deleteLinkDomainEvent = DeleteLinkDomainEvent.of(document.getId());
                deleteLinkDomainEvent.send(pipeline);
            }
        }

        pageStatisticRepository.deleteAllByLinkId(link.getId());

        linkStatisticRepository.deleteAllByLinkId(link.getId());

        linkRepository.deleteById(link.getId());

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
