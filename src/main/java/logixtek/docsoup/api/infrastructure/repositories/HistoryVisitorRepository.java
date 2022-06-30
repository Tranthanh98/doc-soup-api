package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.HistoryVisitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface HistoryVisitorRepository extends JpaRepository<HistoryVisitorEntity, Long> {

    Collection<HistoryVisitorEntity> findAllByLinkIdAndViewerId(UUID linkId, Long viewerId);
}
