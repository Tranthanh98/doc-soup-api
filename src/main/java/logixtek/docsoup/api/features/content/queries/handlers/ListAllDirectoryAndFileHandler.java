package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.content.mappers.DirectoryEntityMapper;
import logixtek.docsoup.api.features.content.mappers.FileEntityMapper;
import logixtek.docsoup.api.features.content.queries.GetDirectory;
import logixtek.docsoup.api.features.content.queries.ListAllDirectoryAndFile;
import logixtek.docsoup.api.features.content.queries.ListAllFile;
import logixtek.docsoup.api.features.content.responses.AggregateContentViewModel;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("ListAllDirectoryAndFileHandler")
@AllArgsConstructor
public class ListAllDirectoryAndFileHandler implements Command.Handler<ListAllDirectoryAndFile,
                                                                ResponseMessageOf<List<AggregateContentViewModel>>> {

    private final Pipeline pipeline;

    @Override
    public ResponseMessageOf<List<AggregateContentViewModel>> handle(ListAllDirectoryAndFile query) {

        List<AggregateContentViewModel> allContents = new ArrayList<>();

        var directoryQuery = GetDirectory.of(query.getDirectoryId());
        directoryQuery.setAccountId(query.getAccountId());
        directoryQuery.setCompanyId(query.getCompanyId());

        var directoriesResult = directoryQuery.execute(pipeline);

        if(directoriesResult.getBody() != null && directoriesResult.getStatusCode() == HttpStatus.OK){
            var directories = Utils.collectionToStream(directoriesResult.getBody())
                    .map(i -> DirectoryEntityMapper.INSTANCE.toViewModel(i))
                    .collect(Collectors.toList());

            allContents.addAll(directories);

        }

        var fileQuery = new ListAllFile(query.getDirectoryId());
        fileQuery.setAccountId(query.getAccountId());
        fileQuery.setCompanyId(query.getCompanyId());

        var fileResult = fileQuery.execute(pipeline);

        if(fileResult.getBody() != null && fileResult.getStatusCode() == HttpStatus.OK){
            var files =Utils.collectionToStream(fileResult.getBody())
                    .map(i -> FileEntityMapper.INSTANCE.toViewModel(i))
                    .collect(Collectors.toList());

            files.sort((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));
            
            allContents.addAll(files);
        }

       return ResponseMessageOf.of(HttpStatus.OK, allContents);
    }
}
