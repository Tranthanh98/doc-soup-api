package logixtek.docsoup.api.infrastructure.middlewares;

import an.awesome.pipelinr.Command;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CommandLogMiddleware implements Command.Middleware {

    @Override
    public <R, C extends Command<R>> R invoke(C command, Command.Middleware.Next<R> next) {
        // log command
        // Handle the log on another issue
        R response = next.invoke();
        // log response
        return response;
    }
}