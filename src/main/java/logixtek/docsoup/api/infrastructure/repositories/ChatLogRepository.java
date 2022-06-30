package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.ChatLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatLogRepository extends JpaRepository<ChatLogEntity, UUID> {
}
