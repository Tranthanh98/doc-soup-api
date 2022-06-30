package logixtek.docsoup.api.features.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.commands.DeleteDirectory;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component("DeleteDirectory")
@AllArgsConstructor
@Getter
@Setter
public class DeleteDirectoryHandler implements Command.Handler<DeleteDirectory, ResponseMessageOf<String>>  {
    private final DirectoryRepository directoryRepository;
    private final PermissionService permissionService;
    private final JobMessageQueuePublisher publisher;
    private static final Long ROOT_FOLDER_PARENT = 0L;
    private static final Integer MINIMUM_NUMBER_OF_FOLDER = 1;
    private static final int EXISTING_NDA_CONTENT_ERROR_CODE = -999;

    @Override
    public ResponseMessageOf<String> handle(DeleteDirectory command) {


        var itemResult = directoryRepository.findById(command.getId());

        if(itemResult.isPresent())
        {
            if(!permissionService.get(itemResult.get(),command).canWrite())
            {
                return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
            }

            var entity = itemResult.get();

            if(entity.getParentId() == ROOT_FOLDER_PARENT && Boolean.TRUE.equals(!entity.getIsTeam())){
                var allPersonalDirectories = directoryRepository
                        .countAllByAccountIdAndParentIdAndCompanyIdAndIsTeamIsFalse(command.getAccountId(), ROOT_FOLDER_PARENT, command.getCompanyId());

                if(allPersonalDirectories <= MINIMUM_NUMBER_OF_FOLDER){
                    return ResponseMessageOf.ofBadRequest("Cannot delete last personal folder.", Map.of());
                }
            }

            var secureIdsOnDirectoryOption = directoryRepository.findAllSecureIdsInDirectory(entity.getId());

            var deleteStatus = directoryRepository.deleteAllById(entity.getId());

            if(deleteStatus == EXISTING_NDA_CONTENT_ERROR_CODE){
                return ResponseMessageOf.ofBadRequest("Folder containing an active or pending NDA cannot be deleted.", Map.of());
            }

            if(secureIdsOnDirectoryOption.isPresent()) {
                List<JobMessage> queueMessages = new ArrayList<>();
                secureIdsOnDirectoryOption.get().forEach(item -> {
                    var jobMessage = new JobMessage<String>();
                    jobMessage.setAction(JobActionConstant.DELETE_DOCUMENT);
                    jobMessage.setDataBody(item);
                    jobMessage.setObjectName(String.class.getName());
                    queueMessages.add(jobMessage);
                });

                publisher.sendMessageBatch(queueMessages);
            }

            return  ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return  ResponseMessageOf.of(HttpStatus.NOT_FOUND);
    }
}
