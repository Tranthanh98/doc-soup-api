package logixtek.docsoup.api.features.administrator.chatlog;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.chatlog.queries.AdminGetAllChatLog;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/chat-log")
public class AdminChatLogController extends BaseAdminController {
    public AdminChatLogController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @GetMapping
    public ResponseEntity<?> getAllChatLog(@RequestParam Integer page, @RequestParam Integer pageSize){
        var query = new AdminGetAllChatLog();
        query.setPage(page);
        query.setPageSize(pageSize);
        return handleWithResponse(query);
    }
}
