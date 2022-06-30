package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.queries.ViewFile;
import lombok.EqualsAndHashCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("ViewFileHandler")
@EqualsAndHashCode(callSuper = true)
public class ViewFileHandler extends BaseViewFileHandler implements Command.Handler<ViewFile, ResponseEntity<UUID>> {

    @Override
    public ResponseEntity<UUID> handle(ViewFile query) {

        var contentResult = this.validate(query);

        if(!contentResult.getSucceeded())
        {
            return  ResponseEntity.badRequest().build();
        }

        var content = contentResult.getData();

        if(content.getFileId()==null)
        {
            return  ResponseEntity.badRequest().build();
        }


        return  this.getLink(query.getLinkId(),null, query.getFileId());
    }

}
