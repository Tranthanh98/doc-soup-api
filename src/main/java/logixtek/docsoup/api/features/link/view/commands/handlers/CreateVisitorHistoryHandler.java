package logixtek.docsoup.api.features.link.view.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.mappers.HistoryVisitorMapper;
import logixtek.docsoup.api.features.link.view.commands.CreateVisitorHistory;
import logixtek.docsoup.api.infrastructure.repositories.HistoryVisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("CreateVisitorHistoryHandler")
@AllArgsConstructor
public class CreateVisitorHistoryHandler implements Command.Handler<CreateVisitorHistory, ResponseEntity<Long>> {

    private final HistoryVisitorRepository historyVisitorRepository;

    @Override
    public ResponseEntity<Long> handle(CreateVisitorHistory command) {
        var entity = HistoryVisitorMapper.INSTANCE.toEntity(command);

        String action = null;

        switch (command.getActionType()){
            case DOWNLOAD_DOCUMENT:
                action = "The document was downloaded on " +command.getBrowserName() +" from " + command.getIpAddress() + " ("+command.getLocation() +")";
                break;
            case VERIFY:
                action = "Username "+command.getEmail() +"entered the requested information in DocSoup on "
                        + command.getBrowserName() +" from " + command.getIpAddress() + " ("+command.getLocation() +")";
                break;
            case AGREED_TERM:
                action = command.getName() + " ("+command.getEmail() + ") agreed to us electronic records and signatures, to DocSoup's Terms of Service, and to the terms of this document on " +
                        command.getBrowserName() + " from " + command.getIpAddress() + " ("+command.getLocation() +")";
                break;
            case AUTHORIZED_TO_READING:
                action = command.getName() + " ("+command.getEmail() + ") was authorized to view the document on " +command.getBrowserName() +
                        " from " + command.getIpAddress() +" ("+command.getLocation() +")";
                break;
            case SIGNED_DOCUMENT:
                action =  command.getName() + " ("+command.getEmail() + ") signed the document on " + command.getBrowserName() +
                        " from " + command.getIpAddress() + " ("+command.getLocation() +")";
                break;
            case GENERATED_SIGNED:
                action = "DocSoup generated the signed PDF";
                break;
            default:
                break;
        }

        entity.setAction(action);

        historyVisitorRepository.saveAndFlush(entity);

        return ResponseEntity.ok(entity.getId());

    }
}
