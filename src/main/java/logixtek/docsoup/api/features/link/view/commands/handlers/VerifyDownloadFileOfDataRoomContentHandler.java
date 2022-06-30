package logixtek.docsoup.api.features.link.view.commands.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.view.commands.VerifyDownloadFileOfDataRoomContent;
import logixtek.docsoup.api.features.link.view.commands.VerifyLink;
import logixtek.docsoup.api.features.share.domainevents.CreateContactDomainEvent;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component("VerifyDownloadFileOfDataRoomContentHandler")
@AllArgsConstructor
public class VerifyDownloadFileOfDataRoomContentHandler implements Command.Handler<VerifyDownloadFileOfDataRoomContent, ResponseMessageOf<String>> {

    private final LinkRepository linkRepository;
    private final LinkStatisticRepository linkStatisticRepository;
    private final Pipeline pipeline;

    @Override
    public ResponseMessageOf<String> handle(VerifyDownloadFileOfDataRoomContent command) {
        var viewerSessionOption = linkStatisticRepository
                .findById(command.getViewerId());

        if (!viewerSessionOption.isPresent()) {
            return ResponseMessageOf.ofBadRequest("Not found viewer",
                    Map.of(VerifyLink.Fields.viewerId, "Not found viewer"));
        }

        var viewerSession = viewerSessionOption.get();

        if (!viewerSession.getDeviceId().equals(command.getDeviceId()) || viewerSession.getAuthorizedAt() == null) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var linkOption = linkRepository
                .findById(viewerSession.getLinkId());

        if(!linkOption.isPresent()){
            return ResponseMessageOf.ofBadRequest("Link not found",
                    Map.of(VerifyDownloadFileOfDataRoomContent.Fields.linkId, "Link not found"));
        }

        var link = linkOption.get();

        if(link.getStatus() > LinkConstant.ACTIVE_STATUS || link.getDocumentId() != null){
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        if(Boolean.TRUE.equals(!link.getDownload())){
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        raiseCreateContactDomainEvent(command, link);

        viewerSession.setDownloadFileToken(UUID.randomUUID().toString());

        linkStatisticRepository.saveAndFlush(viewerSession);

        return ResponseMessageOf.of(HttpStatus.OK, viewerSession.getDownloadFileToken());
    }

    private void raiseCreateContactDomainEvent(VerifyDownloadFileOfDataRoomContent command, LinkEntity link){
        var createContactCommand =
                CreateContactDomainEvent.of(command.getEmail(),
                        command.getName(),
                        link.getCreatedBy(),
                        link.getCompanyId());

        createContactCommand.send(pipeline);
    }
}
