package logixtek.docsoup.api.features.chatbot;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.chatbot.commands.TawkToWebhookRequest;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("tawk-webhooks")
@PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
public class ChatbotController extends BaseController {

    public ChatbotController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService) {
        super(pipeline, authenticationManager, accountService);
    }

    @PostMapping("/webhooks/start-end/{token}")
    public ResponseEntity<?> startChatWebhooks(@PathVariable String token,
                                               @Valid @RequestBody TawkToWebhookRequest command) {

        command.setToken(token);
        return handleWithResponse(command);

    }
}
