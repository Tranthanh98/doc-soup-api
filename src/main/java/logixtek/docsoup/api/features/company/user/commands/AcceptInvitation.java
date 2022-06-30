package logixtek.docsoup.api.features.company.user.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
public class AcceptInvitation implements Command<ResponseMessageOf<Long>> {
    String token;

    Boolean isAccepted = false;
}
