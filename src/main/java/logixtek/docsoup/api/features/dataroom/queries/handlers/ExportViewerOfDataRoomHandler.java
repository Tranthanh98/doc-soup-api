package logixtek.docsoup.api.features.dataroom.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.queries.ExportViewerOfDataRoom;
import logixtek.docsoup.api.features.share.dtos.DataRoomViewerRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.helper.Helper;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component("ExportViewerOfDataRoomHandler")
@AllArgsConstructor
public class ExportViewerOfDataRoomHandler implements Command.Handler<ExportViewerOfDataRoom, ResponseEntity<byte[]>> {
    private static final Logger logger = LoggerFactory.getLogger(ExportViewerOfDataRoomHandler.class);
    private final ContactRepository contactRepository;
    private final DataRoomRepository dataRoomRepository;
    private final PermissionService permissionService;
    private final JobMessageQueuePublisher publisher;

    @SneakyThrows
    @Override
    public ResponseEntity<byte[]> handle(ExportViewerOfDataRoom query) {

        var dataRoomOption = dataRoomRepository.findById(query.getDataRoomId());

        if (!dataRoomOption.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!permissionService.getOfDataRoom(dataRoomOption.get(), query).canRead()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (query.getEmail()) {
            var jobMessage = new JobMessage<DataRoomViewerRequestMessage>();
            jobMessage.setAction(JobActionConstant.EXPORT_DATA_ROOM_VIEWER);
            jobMessage.setDataBody(DataRoomViewerRequestMessage.of(query.getAccountId(), query.getDataRoomId()));
            jobMessage.setObjectName(DataRoomViewerRequestMessage.class.getName());

            publisher.sendMessage(jobMessage);

            return ResponseEntity.ok().build();
        }

        var viewersOfDataRoomOption = contactRepository.findAllViewerByDataRoomId(query.getDataRoomId());

        if (viewersOfDataRoomOption.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var viewersOfDataRoom = viewersOfDataRoomOption.get();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        List<String> headers = Helper.genCsvViewerHeader();

        var out = new ByteArrayOutputStream();
        var csvPrinter = new CSVPrinter(new PrintWriter(out, true, StandardCharsets.UTF_16), format);
        try (csvPrinter) {
            csvPrinter.printRecord(headers);
            for (var viewer : viewersOfDataRoom) {
                csvPrinter.printRecord(Helper.convertToCSVRow(viewer));
            }

            csvPrinter.flush();

            var responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Disposition", "attachment; filename=listViewer.csv");
            responseHeaders.add("fileDownloadName", "listViewer.csv");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentLength(out.toByteArray().length)
                    .contentType(MediaType.valueOf("text/csv"))
                    .body(out.toByteArray());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return ResponseEntity.badRequest().build();
        }
    }
}
