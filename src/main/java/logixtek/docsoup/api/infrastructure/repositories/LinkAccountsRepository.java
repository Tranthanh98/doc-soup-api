package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.LinkAccountsEntity;
import logixtek.docsoup.api.infrastructure.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface LinkAccountsRepository extends JpaRepository<LinkAccountsEntity, Long> {
    Collection<LinkAccountsEntity> findTop10ByCompanyIdAndArchivedIsFalseAndNameContainingOrderByName(UUID companyId, String name);

    Optional<LinkAccountsEntity> findByNameAndCompanyId(String name, UUID companyId);

    Integer countAllByCompanyId(UUID companyId);

    @Query(value = "EXECUTE [dbo].[sel_link_account] :status, :mode, :archived, :companyId, :accountId", nativeQuery = true)
    Collection<LinkAccountWithActivityInfor> findAllByStatusAndModeAndArchivedAndCompanyIdAndAccountId(String status, String mode, Boolean archived, String companyId, String accountId);

    @Query("select l.id as linkId, l.createdDate as createdDate, case when l.status > 0 then true else false end as disabled, f.version as version, concat(a.firstName, ' ', a.lastName) as linkCreatorName, " +
            "sum(ls.visit) as activity, case when l.documentId is not null then f.name else d.name end as linkName, l.refId as refId, l.documentId as documentId " +
            " from LinkAccountsEntity as la " +
            "join LinkEntity as l on l.linkAccountsId = la.id " +
            "left join LinkStatisticEntity as ls on l.id = ls.linkId " +
            "left join FileEntity as f on f.id = l.refId and l.documentId is not null " +
            "left join DataRoomEntity as d on d.id = l.refId and l.documentId is null " +
            "join AccountEntity as a on a.id = l.createdBy " +
            "where la.id = :linkAccountId and la.companyId = :companyId " +
            "and ((:filter = 'content' and l.documentId is not null) or (:filter = 'data-room' and l.documentId is null))" +
            "group by l.id, la.name, l.createdDate, l.status, l.documentId, l.refId, f.name, d.name, f.version, a.firstName, a.lastName")
    Page<LinkWithStatistic> findAllLinkOfLinkAccount(@Param("linkAccountId") Long linkAccountId,
                                                     @Param("companyId") UUID companyId,
                                                     @Param("filter") String filter,
                                                     Pageable pageable);

    @Query(value = "EXECUTE [dbo].[sel_link_account_by_id] :linkAccountId", nativeQuery = true)
    Optional<LinkAccountWithActivityInfor> getLinkAccountsById(Long linkAccountId);

    @Query("select c.email as email, ls.location as location, ls.viewedAt as viewedAt, ls.visit as visits, ls.duration as duration, ls.signedNDA as signedNDA, " +
            "ls.ndaId as ndaId, ls.deviceName as device, ls.contactId as contactId " +
            "from LinkStatisticEntity as ls " +
            "left join ContactEntity as c on ls.contactId = c.id " +
            "join LinkEntity as l on l.id = ls.linkId " +
            "join LinkAccountsEntity as la on la.id = l.linkAccountsId " +
            "where la.id = :linkAccountId and la.companyId = :companyId " +
            "group by  c.email, ls.location, ls.viewedAt, ls.visit, ls.duration, ls.signedNDA, ls.ndaId, ls.deviceName,  ls.contactId")
    Page<LinkAccountVisitor> getVisitorOfLinkAccount(@Param("linkAccountId") Long linkAccountId, @Param("companyId") UUID companyId, Pageable pageable);

    @Procedure(name = "LinkAccountsEntity.mergeLinkAccount")
    void mergeLinkAccount(@Param("sourceLinkAccountId") Long sourceLinkAccountId, @Param("destinationLinkAccountId") Long destinationLinkAccountId);

    @Query(value = "EXECUTE [dbo].[sel_data_room_of_link_account] :linkAccountId", nativeQuery = true)
    Collection<DataRoomInfo> getListDataRoomOfLinkAccount(Long linkAccountId);

    @Query("select la.id as id, la.name as name, COALESCE(MAX(ls.viewedAt), COALESCE(MAX(l.createdDate), la.createdDate)) as lastActivity " +
            "from LinkAccountsEntity la " +
            "left join LinkEntity  l " +
            "on l.linkAccountsId = la.id " +
            "left join LinkStatisticEntity ls " +
            "on ls.linkId = l.id " +
            "where la.companyId =:companyId and la.name LIKE %:name% " +
            "group by la.id, la.name, la.createdDate")
    Page<SimplifiedLinkAccountInfo> findAllByCompanyIdAndNameContains(UUID companyId, String name, Pageable pageable);

    @Query(value = "EXECUTE [dbo].[sel_statictis_visit_by_num_of_day] :accountId, :companyId, :numOfDay", nativeQuery = true)
    Collection<StatisticVisits> findAllStatisticVisitByDay(@Param("accountId") String accountId, @Param("companyId") String companyId, Integer numOfDay);
}