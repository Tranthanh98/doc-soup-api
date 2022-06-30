package logixtek.docsoup.api.features.chatbot.services.impl;

import logixtek.docsoup.api.features.chatbot.services.ChatLogService;
import logixtek.docsoup.api.infrastructure.entities.ChatLogEntity;
import logixtek.docsoup.api.infrastructure.repositories.ChatLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class DefaultChatLogService implements ChatLogService {

    private final ChatLogRepository chatLogRepository;

    @Override
    public void insert(ChatLogEntity chatLogEntity) {

        var oldChat = chatLogRepository.findById(chatLogEntity.getId());
        if (!oldChat.isPresent()) {
            chatLogRepository.save(chatLogEntity);
        } else {
            chatLogEntity.setStartChat(oldChat.get().getStartChat());
            chatLogRepository.saveAndFlush(chatLogEntity);
        }
    }
}
