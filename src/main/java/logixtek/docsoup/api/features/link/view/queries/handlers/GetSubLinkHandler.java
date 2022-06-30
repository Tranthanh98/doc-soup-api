package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.view.queries.GetSubLink;
import logixtek.docsoup.api.features.share.document.commands.RegisterDocument;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("GetSubLinkHandler")
@AllArgsConstructor
public class GetSubLinkHandler implements Command.Handler<GetSubLink, ResponseEntity<UUID>> {

    private  final LinkRepository linkRepository;
    private  final Pipeline pipeline;
    @Override
    public ResponseEntity<UUID> handle(GetSubLink query) {

        var file = query.getFile();
        var parentLink = query.getParentLink();

        var existingLinkOption = linkRepository.findFirstByRefIdAndParentAndDocumentIdIsNotNull(file.getId(), parentLink.getId());

        if(existingLinkOption.isPresent())
        {
            return ResponseEntity.ok(existingLinkOption.get().getId());
        }

        var registerDocumentCommand = RegisterDocument.builder()
                .fileId(file.getId())
                .download(parentLink.getDownload())
                .expiredAt(parentLink.getExpiredAt())
                .build();


        registerDocumentCommand.setAccountId(parentLink.getCreatedBy());

        var resultRegister = registerDocumentCommand.execute(pipeline);

        if(!resultRegister.getSucceeded())
        {
            return  ResponseEntity.internalServerError().build();
        }

        var subLinkEntity = new LinkEntity();
        subLinkEntity.setRefId(file.getId());
        subLinkEntity.setLinkAccountsId(parentLink.getLinkAccountsId());
        subLinkEntity.setDownload(parentLink.getDownload());
        subLinkEntity.setWatermarkId(parentLink.getWatermarkId());
        subLinkEntity.setExpiredAt(parentLink.getExpiredAt());
        subLinkEntity.setParent(parentLink.getId());
        subLinkEntity.setDocumentId(resultRegister.getData());
        subLinkEntity.setCreatedBy(parentLink.getCreatedBy());
        subLinkEntity.setCompanyId(parentLink.getCompanyId());

        subLinkEntity = linkRepository.saveAndFlush(subLinkEntity);

        return  ResponseEntity.ok(subLinkEntity.getId());
    }
}
