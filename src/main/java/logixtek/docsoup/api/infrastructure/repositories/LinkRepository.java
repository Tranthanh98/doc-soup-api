package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.models.SimplifiedLinkInformation;
import logixtek.docsoup.api.infrastructure.models.LinkStatistic;
import logixtek.docsoup.api.infrastructure.models.LinkInformation;
import logixtek.docsoup.api.infrastructure.models.TeamActivityStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<LinkEntity, UUID> {

    @Query("select l from LinkEntity as l " +
            "join FileEntity as f " +
            "on l.refId = f.id " +
            "and l.documentId is not null " +
            "where f.id = :fileId")
    Optional<List<LinkEntity>> findAllByFileId(@Param("fileId") Long fileId);

    @Query("select l.id as id, l.linkAccountsId as linkAccountsId, l.status as status, l.companyId as companyId, l.createdDate as createdDate, l.modifiedDate as modifiedDate, l.createdBy as createdBy, " +
            "l.documentId as documentId, l.download as download, l.expiredAt as expiredAt, l.ndaId as ndaId, l.refId as refId, l.secure as secure, l.visit as visit, l.watermarkId as watermarkId, " +
            "l.parent as parent, la.name as name, concat(a.firstName, ' ', a.lastName) as createdByName " +
            "from LinkEntity as l " +
            "left join LinkAccountsEntity la " +
            "on la.id = l.linkAccountsId " +
            "join DataRoomEntity as d " +
            "on l.refId = d.id " +
            "join AccountEntity as a on a.id = l.createdBy "+
            "and l.documentId is null "+
            "where d.id = :dataRoomId")
    Optional<List<LinkInformation>> findAllByDataRoomId(@Param("dataRoomId") Long dataRoomId);

    Optional<LinkEntity> findFirstByRefIdAndParentAndDocumentIdIsNotNull(Long refId,UUID parent);

    @Query("select l.id as linkId, la.name as linkName, la.id as linkAccountsId, l.createdDate as createdDate, concat(a.firstName, ' ', a.lastName) as linkCreatorName, " +
            "case when l.status > 0 then true else false end as disabled, sum(s.duration) as duration,sum (s.visit)as activity, " +
            "l.refId as refId " +
            "from LinkEntity l  " +
            "left join LinkStatisticEntity s " +
            "on l.id = s.linkId " +
            "left join LinkAccountsEntity la "+
            "on l.linkAccountsId = la.id " +
            "join AccountEntity as a on a.id = l.createdBy "+
            "where l.refId = :fileId and l.documentId is not null "+
            "group by l.id,la.name,la.id,l.createdDate,l.status, l.refId, a.firstName, a.lastName"
            )
    Page<LinkStatistic> findAllLinkWithStatistic(@Param("fileId") Long fileId, Pageable pageable);


    @Transactional
    @Modifying
    @Query("update LinkEntity "
            + "set status = CONVERT(int, :disabled) "
            + "where documentId is NULL "
            + "and refId = :refId " )
    void updateAllDataRoomLinkStatus(@Param("refId") Long dataRoomId, @Param("disabled") Boolean disabled);

    @Query("select l.id as id, l.linkAccountsId as linkAccountsId, l.status as status, l.companyId as companyId, l.createdDate as createdDate, l.modifiedDate as modifiedDate, l.createdBy as createdBy, " +
            "l.documentId as documentId, l.download as download, l.expiredAt as expiredAt, l.ndaId as ndaId, l.refId as refId, l.secure as secure, l.visit as visit, l.watermarkId as watermarkId, l.parent as parent, la.name as name " +
            "from LinkEntity as l " +
            "left join LinkAccountsEntity la " +
            "on la.id = l.linkAccountsId " +
            "where l.id = :id")
    Optional<LinkInformation> findLinkWithLinkAccountById(@Param("id") UUID id);

    boolean existsAllByRefIdAndDocumentIdIsNullAndStatus(Long refId, Integer status);

    @Query("SELECT l.id AS id, l.linkAccountsId AS linkAccountsId, la.name AS name, CONCAT(ac.firstName, ' ', ac.lastName) AS owner, " +
            "la.createdDate AS createDate FROM LinkAccountsEntity la " +
            "INNER JOIN LinkEntity l ON la.id = l.linkAccountsId " +
            "LEFT JOIN AccountEntity ac ON ac.id = la.createdBy " +
            "WHERE la.name LIKE CONCAT('%', :keyword ,'%') AND la.companyId = :companyId AND l.createdBy = :accountId "+
            "AND l.status=0")
    Page<SimplifiedLinkInformation> searchLinkWithKeyword(@Param("keyword") String keyword,
                                                          @Param("companyId") UUID companyId,
                                                          @Param("accountId") String accountId,
                                                          Pageable pageable);

    int countAllByCompanyIdAndStatusAndCreatedDateAfter(UUID companyId, Integer status, OffsetDateTime createdDate);

    @Query("select sum(ls.visit) from LinkStatisticEntity ls " +
            "join LinkEntity as l on l.id = ls.linkId " +
            "where l.companyId = :companyId and l.createdDate > :createdDate " +
            "group by l.companyId")
    Long sumVisit(UUID companyId, OffsetDateTime createdDate);

    Integer countAllByCompanyIdAndStatus(UUID companyId, Integer status);

    @Query("select count(l) as linksCreated,  cast(l.createdDate as date) as createdDate " +
            "from LinkEntity l where (cast(l.createdDate as date) BETWEEN cast(:fromDate as date) AND cast(:toDate as date)) and l.companyId=:companyId " +
            "group by cast(l.createdDate as date) " +
            "order by cast(l.createdDate as date)")
    Optional<List<TeamActivityStatistic>> findAllLinkCreatedByCompanyIdAndCreatedDateBetweenGroupByCreatedDate(@Param("companyId") UUID companyId, @Param("fromDate") OffsetDateTime fromDate, @Param("toDate")  OffsetDateTime toDate);

    Optional<LinkEntity> findFirstByDocumentId(UUID documentId);
}
