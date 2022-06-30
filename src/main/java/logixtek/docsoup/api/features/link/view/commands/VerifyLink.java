package logixtek.docsoup.api.features.link.view.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class VerifyLink implements Command<ResponseMessageOf<LinkResult>> {

    UUID linkId;

    String deviceId;

    Long viewerId;

    String passcode;

    @Email
    String email;

    String name;

    String ip;
}
