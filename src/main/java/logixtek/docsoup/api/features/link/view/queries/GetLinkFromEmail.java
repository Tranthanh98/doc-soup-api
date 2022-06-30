package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@FieldNameConstants
public class GetLinkFromEmail implements Command<ResponseMessageOf<LinkResult>> {
    UUID linkId;

    String deviceId;

    String userAgent;

    Double longitude;

    Double latitude;

    String ip;

    String accountId;

    String token;
}
