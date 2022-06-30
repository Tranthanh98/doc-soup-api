package logixtek.docsoup.api.infrastructure.middlewares;
import an.awesome.pipelinr.Notification;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(1)
public class NotificationLogMiddleware implements Notification.Middleware {

    @Override
    public <N extends Notification> void invoke(N notification, Next next) {
        // Handle wrriting log here.
        next.invoke();
        // stop tx
    }
}