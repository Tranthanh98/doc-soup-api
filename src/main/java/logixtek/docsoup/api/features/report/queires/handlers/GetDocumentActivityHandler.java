package logixtek.docsoup.api.features.report.queires.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.report.queires.GetDocumentActivity;
import logixtek.docsoup.api.infrastructure.models.DocumentActivity;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("DocumentActivityHandler")
@AllArgsConstructor
public class GetDocumentActivityHandler implements Command.Handler<GetDocumentActivity, ResponseMessageOf<List<DocumentActivity>>> {
    private final FileRepository fileRepository;
    private static final int DATE_RECENT = 30;
    @Override
    public ResponseMessageOf<List<DocumentActivity>> handle(GetDocumentActivity query) {
        if(query.getDateRecent() > DATE_RECENT ||
                (!Sort.Direction.valueOf(query.getSortDirection()).equals(Sort.Direction.ASC) && !Sort.Direction.valueOf(query.getSortDirection()).equals(Sort.Direction.DESC))) {
            return ResponseMessageOf.of(HttpStatus.NO_CONTENT);
        }

        var documentActivitiesOption = fileRepository
                .findDocumentActivityByUserIdAndCompanyId(query.getAccountId(), query.getCompanyId().toString(), query.getTop(), query.getSortDirection(),  query.getDateRecent());

        return documentActivitiesOption.map(documentActivities ->
                        ResponseMessageOf.of(HttpStatus.OK, documentActivities))
                .orElseGet(() ->
                        ResponseMessageOf.of(HttpStatus.NO_CONTENT));

    }
}
