package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.models.LinkStatistic;
import logixtek.docsoup.api.infrastructure.models.TeamActivityStatistic;
import logixtek.docsoup.api.infrastructure.models.ViewerLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkStatisticRepository extends JpaRepository<LinkStatisticEntity, Long> {

    Optional<List<LinkStatisticEntity>> findAllByIdIsIn(Collection<Long> ids);
    Optional<LinkStatisticEntity> findFirstByLinkIdAndDeviceId(UUID linkId, String deviceId);

    @Transactional
    @Modifying
    @Query("update LinkStatisticEntity l "+
           "set l.authorizedAt = null "+
            "where l.linkId= :linkId"
             )
    Integer updateAuthorizedAtValue(@Param("linkId") UUID linkId);

    @Query( value = "select s.longitude as longitude,s.latitude as latitude, COUNT(s.longitude) as totalView " +
            "from LinkStatisticEntity s "+
            "inner join LinkEntity l "+
            "on s.linkId = l.id "+
            "left join ContactEntity as c on c.id = s.contactId "+
            "WHERE l.refId = :fileId and l.documentId is not null " +
            "GROUP BY s.longitude, s.latitude")
    Optional<Collection<ViewerLocation>> findAllViewerLocationByFileId(@Param("fileId") Long fileId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update s "+
            "set s.duration = sp.sumDuration, s.viewed_at = GETUTCDATE() from link_statistic AS s WITH(rowlock)"+
            "inner join "+
            "(select p.link_statistic_id, SUM(p.duration) as sumDuration from page_statistic AS p WITH(nolock)"+
            "where p.link_statistic_id=:id and p.link_id= :linkId "+
            "group by p.link_statistic_id) as sp ON s.id = sp.link_statistic_id " +
            "where s.id=:id"
    )
    void sumDuration(@Param("linkId") String linkId, @Param("id") long id);

    @Transactional
    void deleteAllByLinkId(UUID linkId);

    @Query( value = "select s.id as id, a.email as linkCreatorEmail, CONCAT(a.firstName, ' ', a.lastName) as linkCreatorName from LinkStatisticEntity s "+
            "inner join LinkEntity l "+
            "on s.linkId = l.id "+
            "left join AccountEntity as a on a.id = l.createdBy "+
            "WHERE s.sentInformationEmail is not null and s.sentInformationEmail = false and l.documentId is not null and s.authorizedAt is not null")
    Optional<Collection<LinkStatistic>> findAllBySentInformationEmailIsFalse();

    @Query("select sum(s.visit) as visits, cast(s.viewedAt as date) as createdDate from LinkStatisticEntity s " +
            "inner join LinkEntity l on l.id = s.linkId " +
            "where (s.viewedAt BETWEEN :fromDate AND :toDate) and l.companyId=:companyId and s.authorizedAt is not null " +
            "group by cast(s.viewedAt as date) " +
            "order by cast(s.viewedAt as date)")
    Optional<List<TeamActivityStatistic>> findAllVisitsByCompanyIdAndViewedAtBetweenGroupByViewedAt(@Param("companyId") UUID companyId, @Param("fromDate") OffsetDateTime fromDate, @Param("toDate")  OffsetDateTime toDate);
}