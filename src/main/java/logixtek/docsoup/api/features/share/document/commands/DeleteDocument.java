package logixtek.docsoup.api.features.share.document.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class DeleteDocument extends BaseIdentityCommand<Result> {

    @NotNull
    UUID id;
}
