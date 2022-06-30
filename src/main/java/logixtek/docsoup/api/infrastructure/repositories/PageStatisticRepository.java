package logixtek.docsoup.api.infrastructure.repositories;


import logixtek.docsoup.api.infrastructure.entities.PageStatisticEntity;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import logixtek.docsoup.api.infrastructure.models.PageStatisticOnContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PageStatisticRepository extends JpaRepository<PageStatisticEntity, Long> {

    Optional<PageStatisticEntity> findFirstByLinkStatisticIdAndPageAndSessionId(Long linkStatisticId,Integer page,String sessionId);

    @Query("select  p.page as page ,SUM(p.duration) as duration from PageStatisticEntity p " +
            "where p.linkStatisticId =:linkStatisticId " +
            "GROUP by p.page")
    Optional<Collection<PageStatistic>> findAllPageStatisticByLinkStatisticId(Long linkStatisticId);


    @Query("select l.id as linkId, s.id as viewerId, la.name as linkName, p.page as page, SUM(p.duration) as duration,max (s.viewedAt) as viewedAt from PageStatisticEntity p "+
            "INNER JOIN LinkStatisticEntity s " +
            "on p.linkStatisticId = s.id "+
            "INNER JOIN LinkEntity l "+
            "on p.linkId = l.id "+
            "left join LinkAccountsEntity la on l.linkAccountsId = la.id " +
            "INNER JOIN ContactEntity c "+
            "on s.contactId = c.id "+
            "WHERE c.id=:contactId and l.refId = :fileId and l.documentId is not null "+
            "GROUP BY l.id, la.name, p.page, s.id")
    Optional<Collection<PageStatisticOnContact>> findAllPageStatisticByContactIdAndFileId(@Param("contactId") Long contactId, @Param("fileId") Long fileId);

    @Transactional
    void deleteAllByLinkId(UUID linkId);
}
