package logixtek.docsoup.api.features.setting.nda.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.setting.nda.queries.PreviewNDA;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("PreviewNDAHandler")
@AllArgsConstructor
public class PreviewNDAHandler implements Command.Handler<PreviewNDA,
        ResponseEntity<String>> {

    private  final FileRepository repository;
    private  final DocumentRepository documentRepository;
    private final PermissionService permissionService;
    private static final Logger logger = LoggerFactory.getLogger(PreviewNDAHandler.class);


    @Override
    public ResponseEntity<String> handle(PreviewNDA query) {

        var itemOption = repository.findById(query.getId());

        if(!itemOption.isPresent())
        {
            return  ResponseEntity.notFound().build();
        }

        var item = itemOption.get();

        if(permissionService.getOfFile(item,query).isDenied() || !item.getNda())
        {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var documentOption = documentRepository.findByFileIdWithVersion(item.getId());

        if(!documentOption.isPresent())
        {
            logger.error("The file " + item.getId() +" has no document");
            ResponseEntity.badRequest().build();
        }

        return  ResponseEntity.ok(documentOption.get().getSecureId());

    }
}
