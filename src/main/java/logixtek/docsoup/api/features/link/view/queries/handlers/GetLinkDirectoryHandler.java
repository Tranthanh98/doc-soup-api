package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.view.commands.ValidateViewRequest;
import logixtek.docsoup.api.features.link.view.queries.GetLinkDirectory;
import logixtek.docsoup.api.features.link.view.responses.LinkDirectory;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetLinkDirectoryHandler")
@AllArgsConstructor
public class GetLinkDirectoryHandler implements Command.Handler<GetLinkDirectory, ResponseEntity<LinkDirectory>> {

    private  final DirectoryRepository directoryRepository;
    private  final FileRepository fileRepository;
    private  final Pipeline pipeline;

    @Override
    public ResponseEntity<LinkDirectory> handle(GetLinkDirectory query) {

        var validateRequest= ValidateViewRequest.of(query.getLinkId(),query.getContentId(),query.getDeviceId(),query.getViewerId());
        var contentResult = validateRequest.execute(pipeline);

        if(!contentResult.getSucceeded())
        {
            return  ResponseEntity.badRequest().build();
        }

        var content = contentResult.getData();

        if(content.getDirectoryId()==null)
        {
            return  ResponseEntity.badRequest().build();
        }

        var directoryOption = directoryRepository.findById(content.getDirectoryId());

        if (!directoryOption.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        var directory = directoryOption.get();

        if(!content.getDirectoryId().equals(query.getDirectoryId())) {

            var childDirectories = directoryRepository.findAllChildrenOfParent(directory.getId());

            if (!childDirectories.stream().anyMatch(x -> x.equals(query.getDirectoryId()))) {
                return ResponseEntity.notFound().build();
            }
        }

        var subDirectoryOption = directoryRepository.findAllByParentId(query.getDirectoryId());

        var fileOption = fileRepository.findAllByDirectoryIdAndCompanyId(query.getDirectoryId(),directory.getCompanyId());

        return ResponseEntity.ok(LinkDirectory.of(query.getContentId(),query.getDirectoryId(),subDirectoryOption.get(),fileOption.get()));


    }
}
