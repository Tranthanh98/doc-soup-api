package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.content.queries.ExportViewerOfFile;
import logixtek.docsoup.api.features.content.queries.ListViewerOfFile;
import logixtek.docsoup.api.features.share.dtos.ExportFileVisitorRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.helper.Helper;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
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

@AllArgsConstructor
@Component("ExportViewerOfFileHandler")
public class ExportViewerOfFileHandler implements Command.Handler<ExportViewerOfFile, ResponseEntity<byte[]>>  {
    private final FileRepository fileRepository;
    private final PermissionService permissionService;
    private final Pipeline pipeline;
    private final JobMessageQueuePublisher publisher;
    private static final int PAGE_SIZE = 2000;

    private static final Logger logger = LoggerFactory.getLogger(ExportViewerOfFileHandler.class);

    @SneakyThrows
    @Override
    public ResponseEntity<byte[]> handle(ExportViewerOfFile query) {

        var fileOption = fileRepository.findById(query.getFileId());

        if (!fileOption.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!permissionService.getOfFile(fileOption.get(), query).canRead()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (query.getEmail()) {
            var jobMessage = new JobMessage<ExportFileVisitorRequestMessage>();
            jobMessage.setAction(JobActionConstant.EXPORT_FILE_VISITOR);
            jobMessage.setDataBody(ExportFileVisitorRequestMessage.of(query.getAccountId(), query.getFileId()));
            jobMessage.setObjectName(ExportFileVisitorRequestMessage.class.getName());

            publisher.sendMessage(jobMessage);

            return ResponseEntity.ok().build();
        }

        var getViewerOfFile = new ListViewerOfFile(query.getFileId());
        getViewerOfFile.setAccountId(query.getAccountId());
        getViewerOfFile.setCompanyId(query.getCompanyId());
        getViewerOfFile.setPage(0);
        getViewerOfFile.setPageSize(PAGE_SIZE);
        
        var viewerOfFiles = getViewerOfFile.execute(pipeline);

        var resultBody = viewerOfFiles.getBody();

        if(viewerOfFiles.getStatusCode() != HttpStatus.OK || !viewerOfFiles.hasBody() || resultBody == null ||  resultBody.getItems() == null ) {
            return ResponseEntity.noContent().build();
        }
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        List<String> headers = Helper.genCsvViewerHeader();

        var out = new ByteArrayOutputStream();
        var csvPrinter = new CSVPrinter(new PrintWriter(out, true, StandardCharsets.UTF_16), format);

        try (csvPrinter) {
            csvPrinter.printRecord(headers);

            if(resultBody.getItems() != null){
                for(var viewer: resultBody.getItems()) {
                    csvPrinter.printRecord(Helper.convertToCSVRow(viewer));
                }
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
        }catch (IOException ex){
            logger.error(ex.getMessage(),ex);
            return ResponseEntity.notFound().build();
        }
    }
}