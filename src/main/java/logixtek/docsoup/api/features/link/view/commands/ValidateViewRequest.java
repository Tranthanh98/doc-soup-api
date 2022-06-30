package logixtek.docsoup.api.features.link.view.commands;


import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ValidateViewRequest implements Command<ResultOf<DataRoomContentEntity>> {
    UUID linkId;
    Long contentId;
    String deviceId;
    Long viewerId ;
}
