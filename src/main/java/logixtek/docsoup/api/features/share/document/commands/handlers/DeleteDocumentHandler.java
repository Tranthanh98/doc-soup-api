package logixtek.docsoup.api.features.share.document.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.share.document.commands.DeleteDocument;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.models.Result;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component("DeleteDocumentHandler")
@AllArgsConstructor
public class DeleteDocumentHandler implements Command.Handler<DeleteDocument, Result> {

    private final DocumentRepository documentRepository;
    private final JobMessageQueuePublisher publisher;

    @SneakyThrows
    @Override
    public Result handle(DeleteDocument deleteDocument) {

        var documentOption = documentRepository.findById(deleteDocument.getId());

        if(documentOption.isPresent())
        {
            var jobMessage = new JobMessage<String>();
            jobMessage.setAction(JobActionConstant.DELETE_DOCUMENT);
            jobMessage.setDataBody(documentOption.get().getSecureId());
            jobMessage.setObjectName(String.class.getName());
            publisher.sendMessage(jobMessage);

            documentRepository.deleteById(deleteDocument.getId());
        }

        return new Result(true, "the document has been deleted");
    }
}
