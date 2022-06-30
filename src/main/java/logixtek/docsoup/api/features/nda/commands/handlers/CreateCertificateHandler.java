package logixtek.docsoup.api.features.nda.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.nda.builders.CertificateNda;
import logixtek.docsoup.api.features.nda.commands.CreateCertificate;
import logixtek.docsoup.api.infrastructure.entities.HistoryVisitorEntity;
import logixtek.docsoup.api.infrastructure.repositories.HistoryVisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

@Component("CreateCertificateHandler")
@AllArgsConstructor
public class CreateCertificateHandler implements Command.Handler<CreateCertificate, Optional<byte[]>> {

    private final HistoryVisitorRepository historyVisitorRepository;

    @Override
    public Optional<byte[]> handle(CreateCertificate command) {

        var results = historyVisitorRepository
                .findAllByLinkIdAndViewerId(command.getLinkId(), command.getViewerId());

        ArrayList<String[]> rowData = new ArrayList<>();

        //create history (array 2d)
        // {
        //      {"timestamp1", "action1", "userAgent1"},
        //      {"timestamp2", "action2", "userAgent2"},
        // }

        for (HistoryVisitorEntity history: results) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
            String[] data = {history.getTimestamp().format(formatter), history.getAction(), history.getUserAgent()};

            rowData.add(data);
        }

        String[][] contentHistory = new String[rowData.size()][3];

        for(int i = 0; i< contentHistory.length; i++){
            for(int j = 0; j < contentHistory[i].length; j++){
                contentHistory[i][j] = rowData.get(i)[j];
            }
        }

        var out = new CertificateNda()
                .documentId(command.getDocumentId())
                .documentName(command.getDocumentName())
                .signedChecksum(command.getSignedChecksum())
                .originalChecksum(command.getOriginalChecksum())
                .signature(command.getSignature())
                .pageCount(command.getPageCount())
                .thumbnail(command.getThumbnail())
                .contentHistory(contentHistory)
                .build();

        if (out != null) {
            var byteArray = out.toByteArray();

            return Optional.of(byteArray);

        }

        return Optional.empty();
    }
}
