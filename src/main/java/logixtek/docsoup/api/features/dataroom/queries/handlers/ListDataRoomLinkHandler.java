package logixtek.docsoup.api.features.dataroom.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.queries.ListDataRoomLink;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.LinkInformation;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("ListDataRoomLinkHandler")
@AllArgsConstructor
public class ListDataRoomLinkHandler implements Command.Handler<ListDataRoomLink,
        ResponseEntity<Collection<LinkInformation>>> {

    private final DataRoomRepository dataRoomRepository;
    private final PermissionService permissionService;
    private final LinkRepository linkRepository;

    @Override
    public ResponseEntity<Collection<LinkInformation>> handle(ListDataRoomLink query) {

        var dataRoomOption = dataRoomRepository.findById(query.getDataRoomId());

        if (!dataRoomOption.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (permissionService.getOfDataRoom(dataRoomOption.get(), query).canRead()) {

            var resultOption = linkRepository.findAllByDataRoomId(query.getDataRoomId());

            if (resultOption.isPresent()) {
                return ResponseEntity.ok(resultOption.get());
            }
        }

        return ResponseEntity.ok(Collections.emptyList());

    }
}
