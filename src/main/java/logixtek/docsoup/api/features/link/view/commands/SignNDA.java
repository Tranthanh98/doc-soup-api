package logixtek.docsoup.api.features.link.view.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class SignNDA implements Command<ResponseMessageOf<LinkResult>> {
    UUID linkId;

    String deviceId;

    Long viewerId;

    Boolean signedNDA = false;

    String ip;
}
