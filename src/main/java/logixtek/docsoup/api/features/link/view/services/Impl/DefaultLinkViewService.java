package logixtek.docsoup.api.features.link.view.services.Impl;

import logixtek.docsoup.api.features.link.view.queries.GetLink;
import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.features.link.view.services.LinkViewService;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.WatermarkRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@AllArgsConstructor
public class DefaultLinkViewService implements LinkViewService {
    private  final DocumentRepository documentRepository;
    private  final WatermarkRepository watermarkRepository;
    private  final DataRoomContentRepository dataRoomContentRepository;
    private final DataRoomRepository dataRoomRepository;

    @Override
    public ResponseMessageOf<LinkResult> fromFileLink(LinkEntity link, Long viewerId) {
        var documentOption = documentRepository.findById(link.getDocumentId());

        if (documentOption.isPresent()) {

            var result = LinkResult.builder().ready(true).viewerId(viewerId).fileId(link.getRefId()).docId(documentOption.get().getSecureId())
                    .build();

            if (link.getWatermarkId() != null && link.getWatermarkId() > 0) {
                var watermarkOption = watermarkRepository.findById(link.getWatermarkId());
                if (watermarkOption.isPresent()) {
                    result.setWatermark(watermarkOption.get().getText());
                }
            }

            return ResponseMessageOf.of(HttpStatus.OK, result);

        } else {
            return ResponseMessageOf.ofBadRequest("Sorry, Your document is unavailable ",
                    Map.of(GetLink.Fields.linkId, "Your document is unavailable"));
        }
    }

    @Override
    public ResponseMessageOf<LinkResult> fromDataRoomLink(LinkEntity link, Long viewerId) {

        var result = LinkResult.builder().ready(true).viewerId(viewerId)
                .build();

        var dataRoomOption = dataRoomRepository.findById(link.getRefId());

        if(dataRoomOption.isPresent()){
            result.setViewType(dataRoomOption.get().getViewType());
            result.setDataRoomName(dataRoomOption.get().getName());
        }

        if (link.getWatermarkId() != null && link.getWatermarkId() > 0) {
            var watermarkOption = watermarkRepository.findById(link.getWatermarkId());
            if (watermarkOption.isPresent()) {
                result.setWatermark(watermarkOption.get().getText());
            }
        }

        var directoryOption = dataRoomContentRepository.findAllDirectoryByDataRoomIdAndIsActiveIsTrue(link.getRefId());

        var fileOption = dataRoomContentRepository.findAllFileByDataRoomIdAndIsActiveIsTrue(link.getRefId());

        if(directoryOption.isPresent() && !directoryOption.isEmpty()) {

            result.setDirectories(directoryOption.get());
        }

        if(fileOption.isPresent() && !fileOption.isEmpty())
        {
            result.setFiles(fileOption.get());
        }

        return ResponseMessageOf.of(HttpStatus.OK, result);
    }

}
