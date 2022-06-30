package logixtek.docsoup.api.features.setting.nda.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.setting.nda.queries.GetAllNDA;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("GetAllNDAHandler")
@AllArgsConstructor
public class GetAllNDAHandler implements Command.Handler<GetAllNDA,
        ResponseEntity<List<FileEntity>>> {

    private  final FileRepository repository;
    @Override
    public ResponseEntity<List<FileEntity>> handle(GetAllNDA query) {

        var itemOption = repository.findAllByAccountIdAndCompanyIdAndNdaIsTrue(query.getAccountId(),query.getCompanyId());

        if(itemOption.isPresent())
        {
            return  ResponseEntity.ok(itemOption.get());
        }

        return ResponseEntity.ok().build();
    }
}
