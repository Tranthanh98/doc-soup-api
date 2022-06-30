package logixtek.docsoup.api.features.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;

@RequiredArgsConstructor(staticName = "of")
public class DeleteDirectory extends BaseIdentityCommand<ResponseMessageOf<String>> {
    @Getter
    @NonNull
    @Min(1)
    private Long id;
}
