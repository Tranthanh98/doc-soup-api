package logixtek.docsoup.api.features.chatbot.commands.handlers;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.chatbot.commands.TawkToWebhookRequest;
import logixtek.docsoup.api.features.chatbot.services.ChatLogService;
import logixtek.docsoup.api.infrastructure.entities.ChatLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("TawkToWebhookRequestHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TawkToWebhookRequestHandler implements Command.Handler<TawkToWebhookRequest, ResponseEntity<String>> {

    private final ChatLogService chatLogService;
    @Value("${tawk.webhook.token}")
    String token;

    @Override
    public ResponseEntity<String> handle(TawkToWebhookRequest query) {

        if (Strings.isNullOrEmpty(query.getToken()) || !query.getToken().equals(token)) {
            return ResponseEntity.badRequest().build();
        }
        var chatEvent = new ChatLogEntity();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        if (query.getEvent().equals("ticket:create")) {
            return ResponseEntity.accepted().build();
        }

        try {

            chatEvent.setId(query.getChatId());
            if (query.getEvent().equals("chat:start")) {
                chatEvent.setStartChat(query.getTime());
            }
            if (query.getEvent().equals("chat:end")) {
                chatEvent.setEndChat(query.getTime());
            }
            chatEvent.setProperty(ow.writeValueAsString(query.getProperty()));
            chatEvent.setVisitor(ow.writeValueAsString(query.getVisitor()));
            chatLogService.insert(chatEvent);
            return ResponseEntity.accepted().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
