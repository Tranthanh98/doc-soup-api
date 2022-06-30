package logixtek.docsoup.api.features.link.view.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.enums.ViewerAction;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Data
@Builder
public class CreateVisitorHistory implements Command<ResponseEntity<Long>> {
    UUID linkId;
    Long viewerId;
    ViewerAction actionType;
    String name;
    String email;
    String ipAddress;
    String location;
    String userAgent;
    String browserName;
}
