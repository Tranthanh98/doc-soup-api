package logixtek.docsoup.api.features.link.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class DeleteLink extends BaseIdentityCommand<ResponseMessageOf<String>> {
    UUID linkId;
}
