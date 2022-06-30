package logixtek.docsoup.api.features.company.user.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.company.user.queries.ExportUsers;
import logixtek.docsoup.api.features.company.user.queries.ListCompanyUser;
import logixtek.docsoup.api.infrastructure.helper.Helper;
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

@Component("ExportUsersHandler")
@AllArgsConstructor
public class ExportUsersHandler implements Command.Handler<ExportUsers, ResponseEntity<byte[]>> {
    private  final Pipeline pipeline;
    private static final Logger logger = LoggerFactory.getLogger(ExportUsersHandler.class);
    @SneakyThrows
    @Override
    public ResponseEntity<byte[]> handle(ExportUsers query) {
        var listCompanyUser = ListCompanyUser.of(query.getCompanyId());
        listCompanyUser.setAccountId(query.getAccountId());
        var allCompanyUsersResult = listCompanyUser.execute(pipeline);
        if(allCompanyUsersResult.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.noContent().build();
        }

        var allCompanyUsers = allCompanyUsersResult.getBody();


        if(allCompanyUsers == null){
            return ResponseEntity.notFound().build();
        }

        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        List<String> headers = Helper.genCsvCompanyUserHeader();
        var out = new ByteArrayOutputStream();
        var csvPrinter = new CSVPrinter(new PrintWriter(out,true, StandardCharsets.UTF_16), format);

        try ( csvPrinter ) {
             csvPrinter.printRecord(headers);

            for(var companyUser: allCompanyUsers) {
                csvPrinter.printRecord(Helper.convertToCSVRow(companyUser));
            }

            csvPrinter.flush();

            var responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Disposition", "attachment; filename=listUsers.csv");
            responseHeaders.add("fileDownloadName", "listUsers.csv");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentLength(out.toByteArray().length)
                    .contentType(MediaType.valueOf("text/csv"))
                    .body(out.toByteArray());
        }  catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            return ResponseEntity.badRequest().build();
        }

    }
}
