package logixtek.docsoup.api.features.administrator.chatlog.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.chatlog.queries.AdminGetAllChatLog;
import logixtek.docsoup.api.infrastructure.entities.ChatLogEntity;
import logixtek.docsoup.api.infrastructure.repositories.ChatLogRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("AdminGetAllChatLogHandler")
public class AdminGetAllChatLogHandler implements Command.Handler<AdminGetAllChatLog, ResponseEntity<PageResultOf<ChatLogEntity>>> {

    private final ChatLogRepository chatLogRepository;

    @Override
    public ResponseEntity<PageResultOf<ChatLogEntity>> handle(AdminGetAllChatLog query) {

        Pageable pageable = PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, "startChat"));

        var listChat = chatLogRepository.findAll(pageable);

        var result = PageResultOf.of(listChat.getContent(),
                query.getPage(),
                listChat.getTotalElements(),
                listChat.getTotalPages());

        return ResponseEntity.ok(result);
    }
}
