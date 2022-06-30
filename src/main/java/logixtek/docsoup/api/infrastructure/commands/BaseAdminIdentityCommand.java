package logixtek.docsoup.api.infrastructure.commands;

import an.awesome.pipelinr.Command;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class BaseAdminIdentityCommand<T> implements Command<T> {
    @Getter
    @Setter
    public String internalAccountId;
}
