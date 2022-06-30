package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.DeniedVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface DeniedVisitRepository extends JpaRepository<DeniedVisitEntity, Long> {
    Optional<Collection<DeniedVisitEntity>> findAllByLinkIdAndSentEmailIsFalse(UUID linkId);

    @Query("select distinct d.linkId from DeniedVisitEntity d where d.sentEmail = false")
    Optional<Collection<UUID>> findAllLinkIdDistinctBySentEmailIsFalse();
}
