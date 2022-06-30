package logixtek.docsoup.api.features.administrator.chatlog.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationAdminCommand;
import logixtek.docsoup.api.infrastructure.entities.ChatLogEntity;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
public class AdminGetAllChatLog extends PaginationAdminCommand<ResponseEntity<PageResultOf<ChatLogEntity>>> {
}
