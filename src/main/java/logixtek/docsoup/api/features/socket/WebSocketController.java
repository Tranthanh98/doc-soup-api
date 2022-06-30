package logixtek.docsoup.api.features.socket;

import logixtek.docsoup.api.features.share.dtos.RefreshViewerMessage;
import logixtek.docsoup.api.features.share.dtos.VisitLinkMessage;
import logixtek.docsoup.api.infrastructure.constants.WebSocketConstant;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequestMapping("ws")
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate template;

    @MessageMapping("/addViewer")
    @SendTo("/topic/viewLink")
    public WebSocketMessage<VisitLinkMessage> viewLink(@Payload WebSocketMessage<VisitLinkMessage> message) {
        return message;
    }

    @MessageMapping("/refresh")
    @SendTo("/topic/refreshAllViewer")
    public WebSocketMessage<RefreshViewerMessage> refreshAllViewer(@Payload WebSocketMessage<RefreshViewerMessage> message) {
        return message;
    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        ViewerStopViewLink(event.getSessionId());
    }

    private void ViewerStopViewLink(String sessionId) {
        var viewerIdAndFileId = sessionId.split("_");
        if(viewerIdAndFileId.length == 2 && Utils.isNumeric(viewerIdAndFileId[0]) && Utils.isNumeric(viewerIdAndFileId[1])) {
            var visitLinkMessage = new VisitLinkMessage();
            visitLinkMessage.setViewerId(Long.parseLong(viewerIdAndFileId[0]));
            visitLinkMessage.setFileId(Long.parseLong(viewerIdAndFileId[1]));
            visitLinkMessage.setIsViewing(false);

            var message = new WebSocketMessage<VisitLinkMessage>();
            message.setAction(WebSocketConstant.VISIT_LINK_ACTION);
            message.setDataBody(visitLinkMessage);

            template.convertAndSend("/topic/viewLink", message);
        }
    }
}