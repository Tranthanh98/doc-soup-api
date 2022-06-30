package logixtek.docsoup.api.infrastructure.commands;

import an.awesome.pipelinr.Command;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.UUID;

@NoArgsConstructor
public class BaseIdentityCommand<T> implements Command<T> {
    @Getter
    @Setter
    public String accountId;

    @Getter
    @Setter
    @Type(type = "uuid-char")
    public UUID companyId;
}

