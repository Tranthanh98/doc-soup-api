package logixtek.docsoup.api.features.dataroom.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.queries.Get;
import logixtek.docsoup.api.features.dataroom.responses.DataRoomContentDirectory;
import logixtek.docsoup.api.features.dataroom.responses.DataRoomContentFile;
import logixtek.docsoup.api.features.dataroom.responses.DataRoomDetail;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("GetDataRoomHandler")
@AllArgsConstructor
public class GetHandler implements Command.Handler<Get, ResponseEntity<DataRoomDetail>> {

    private final DataRoomRepository dataRoomRepository;
    private final DataRoomContentRepository dataRoomContentRepository;
    private final AccountRepository accountRepository;
    private final LinkRepository linkRepository;

    @Override
    public ResponseEntity<DataRoomDetail> handle(Get query) {
        var dataRoomOption = dataRoomRepository.findById(query.getId());

        if (dataRoomOption.isPresent()) {
            var dataRoom = dataRoomOption.get();

            var accountOption = accountRepository.findById(dataRoom.getAccountId());

            var dataRoomContent = dataRoomContentRepository.findAllByDataRoomId(query.getId());

            var isDisabledAllLink = !linkRepository.existsAllByRefIdAndDocumentIdIsNullAndStatus(dataRoom.getId(), LinkConstant.ACTIVE_STATUS);

            var responseData = DataRoomDetail.of(dataRoom.getId(),
                    dataRoom.getName(),
                    dataRoom.getViewType(),
                    Strings.EMPTY,
                    Strings.EMPTY,
                    isDisabledAllLink,
                    dataRoom.getCompanyId(),
                    dataRoom.getAccountId(),
                    new ArrayList<>(),
                    new ArrayList<>());

            if (accountOption.isPresent()) {
                var ownerFullName = accountOption.get().getFirstName().concat(" ").concat(accountOption.get().getLastName());
                responseData.setOwnerFullName(ownerFullName);
                responseData.setOwnerEmail(accountOption.get().getEmail());
            }

            if (dataRoomContent.isPresent() && !dataRoomContent.isEmpty()) {

                for (Object[] result : dataRoomContent.get()) {
                    DirectoryEntity dir = (DirectoryEntity) result[0];
                    FileEntity file = (FileEntity) result[1];
                    if (dir != null && dir.getId() > 0) {
                        var content = (DataRoomContentEntity) result[2];
                        var dataRoomContentDirectory = DataRoomContentDirectory.of(content.getId(), content.getIsActive(), content.getOrderNo(), dir);
                        responseData.getDirectories().add(dataRoomContentDirectory);
                    }

                    if (file != null && file.getId() > 0) {
                        var content = (DataRoomContentEntity) result[2];
                        var dataRoomContentFile = DataRoomContentFile.of(content.getId(), content.getIsActive(), content.getOrderNo(), file);

                        responseData.getFiles().add(dataRoomContentFile);
                    }
                }

                return ResponseEntity.ok(responseData);
            }

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();

    }
}
