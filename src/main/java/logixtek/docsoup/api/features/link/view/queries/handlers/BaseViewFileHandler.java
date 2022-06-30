package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.view.commands.ValidateViewRequest;
import logixtek.docsoup.api.features.link.view.queries.BaseViewFile;
import logixtek.docsoup.api.features.link.view.queries.GetSubLink;
import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.UUID;


@NoArgsConstructor
public abstract class BaseViewFileHandler{

    @Autowired
    protected Pipeline pipeline;
    @Autowired
    protected LinkRepository linkRepository;
    @Autowired
    protected FileRepository fileRepository;

    protected ResultOf<DataRoomContentEntity> validate(BaseViewFile query) {

        var validateRequest= ValidateViewRequest.of(query.getLinkId(),query.getContentId(),query.getDeviceId(),query.getViewerId());
        return validateRequest.execute(pipeline);

    }

    protected Boolean ensureFileBelongInToDirectory(Long directoryId, FileEntity file)
    {
        return  directoryId == null || file.getDirectoryId()==directoryId;
    }

    protected ResponseEntity<UUID> getLink(UUID linkId,Long directoryId, Long fileId) {

        var linkOption = linkRepository.findLinkWithLinkAccountById(linkId);

        var link = linkOption.get();

        var fileOption = fileRepository.findById(fileId);

        if(!fileOption.isPresent())
        {
            return  ResponseEntity.badRequest().build();
        }

        var file = fileOption.get();

        if(!ensureFileBelongInToDirectory(directoryId,file))
        {
            return  ResponseEntity.badRequest().build();
        }

        var getSubLink = GetSubLink.of(link,file);

        return  getSubLink.execute(pipeline);
    }

}