package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import logixtek.docsoup.api.infrastructure.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<ContactEntity, Long>{

    Optional<ContactEntity> findFirstByEmailAndCompanyId(String email, UUID companyId);

    @Query(value = "EXECUTE [dbo].[sel_contact] :companyId, :accountId, :mode, :archived", nativeQuery = true)
    Collection<ContactWithLinkModel> findAllContactWithLinkModelByCompanyIdAndAccountIdAndModeAndArchived(String companyId, String accountId, String mode, Boolean archived);


    @Query("select s.id as viewerId, c.id as contactId, c.email as email, a.email as linkCreatorEmail, s.viewedAt as viewedAt,s.deviceName as device, s.isPreview as isPreview, " +
            "s.location as locationName, la.name as sender,s.duration as duration, s.signedNDA as signedNDA, s.ndaId as ndaId, s.verifiedEmail as verifiedEmail, s.downloaded as downloaded, " +
            "CASE WHEN COUNT(p.page) = 0.0 THEN 0 ELSE (COUNT(distinct CASE WHEN p.duration > 0 THEN p.page ELSE null END) * 100/COUNT(distinct p.page)) END as viewedRate, CONCAT(a.firstName, ' ', a.lastName) as linkCreator " +
            "from LinkStatisticEntity s " +
            "inner join LinkEntity l on s.linkId = l.id " +
            "left join LinkAccountsEntity la on l.linkAccountsId = la.id " +
            "left join PageStatisticEntity p on p.linkStatisticId = s.id " +
            "left join ContactEntity c on s.contactId = c.id " +
            "left join AccountEntity a on l.createdBy = a.id " +
            "WHERE l.id = :linkId " +
            "GROUP BY s.id, c.id, c.email, a.email, s.viewedAt, s.deviceName, s.isPreview, s.location, la.name, s.duration, s.signedNDA, s.ndaId, s.verifiedEmail, s.downloaded, a.firstName, a.lastName")
    Optional<Collection<Viewer>> findAllViewerByLinkId(@Param("linkId") UUID linkId);

    @Query("select s.id as viewerId, c.id as contactId, c.email as email, a.email as linkCreatorEmail, s.viewedAt as viewedAt,s.deviceName as device, s.isPreview as isPreview, " +
            "s.location as locationName,la.name as sender,s.duration as duration, s.signedNDA as signedNDA, s.ndaId as ndaId, s.verifiedEmail as verifiedEmail, s.downloaded as downloaded, " +
            "CASE WHEN COUNT(p.page) = 0.0 THEN 0 ELSE (COUNT(distinct CASE WHEN p.duration > 0 THEN p.page ELSE null END) * 100/COUNT(distinct p.page)) END as viewedRate, CONCAT(a.firstName, ' ', a.lastName) as linkCreator " +
            "from LinkStatisticEntity s " +
            "inner join LinkEntity l on s.linkId = l.id " +
            "left join LinkAccountsEntity la on l.linkAccountsId = la.id " +
            "left join PageStatisticEntity p on p.linkStatisticId = s.id " +
            "left join ContactEntity c on s.contactId = c.id " +
            "left join AccountEntity a on l.createdBy = a.id " +
            "WHERE l.refId = :fileId and l.documentId is not null and s.authorizedAt is not null " +
            "GROUP BY s.id, c.id, c.email, a.email, s.viewedAt, s.deviceName, s.isPreview, s.location, la.name, s.duration, s.signedNDA, s.ndaId, s.verifiedEmail, s.downloaded, a.firstName, a.lastName")
    Page<Viewer> findAllViewerByFileId(@Param("fileId") Long fileId, Pageable pageRequest);

    @Query("select s.id as viewerId, c.id as contactId, c.email as email, s.viewedAt as viewedAt,s.deviceName as device, " +
            "s.location as locationName,la.name as sender " +
            "from LinkStatisticEntity s " +
            "inner join LinkEntity l on s.linkId = l.id " +
            "left join LinkAccountsEntity la on l.linkAccountsId = la.id " +
            "left join ContactEntity c on s.contactId = c.id " +
            "WHERE l.refId = :dataRoomId and l.documentId is null and s.contactId is not null")
    Optional<Collection<Viewer>> findAllViewerByDataRoomId(@Param("dataRoomId") Long dataRoomId);

    @Query("SELECT l.id as id, s.id as viewerId, la.name as linkName, " +
            "CASE WHEN EXISTS " +
            "(SELECT id FROM LinkStatisticEntity ls WHERE l.id = ls.linkId and ls.contactId = :contactId and ls.signedNDA=true) " +
            "THEN true ELSE false END AS signedNDA " +
            "FROM LinkEntity l " +
            "LEFT JOIN LinkAccountsEntity la ON l.linkAccountsId = la.id " +
            "INNER JOIN LinkStatisticEntity s " +
            "ON l.id = s.linkId " +
            "INNER JOIN ContactEntity c ON c.id = s.contactId " +
            "WHERE c.id = :contactId " +
            "GROUP BY c.id, l.id, s.id, la.name ")
    Optional<Collection<ContactDetailWithLinkModel>> findAllLinkNameAndSignedNDAByContactId(@Param("contactId") Long contactId);

    @Query("select s.id as viewerId, s.viewedAt as viewedAt, s.deviceName as device, c.name as contactName, " +
            "s.duration as duration, f.name as fileName, " +
            "CASE WHEN COUNT(p.page) = 0.0 THEN 0 ELSE (COUNT(distinct CASE WHEN p.duration > 0 THEN p.page ELSE null END) * 100/COUNT(distinct p.page)) END as viewedRate " +
            "from LinkStatisticEntity s " +
            "inner join LinkEntity l on s.linkId = l.id " +
            "inner join FileEntity  f on f.id = l.refId " +
            "left join LinkAccountsEntity la on l.linkAccountsId = la.id " +
            "left join PageStatisticEntity p on p.linkStatisticId = s.id " +
            "left join ContactEntity c on s.contactId = c.id " +
            "WHERE (s.viewedAt BETWEEN :startDate AND :endDate) and f.companyId=:companyId and l.createdBy=:accountId and l.documentId is not null and s.authorizedAt is not null " +
            "GROUP BY s.id, c.id, s.viewedAt, s.duration, f.id, f.name, c.name, s.deviceName")
    Optional<Collection<SimplifiedViewer>> findAllViewerVisitDocumentByCompanyIdAndAccountIdBetweenDate(@Param("companyId") UUID companyId, @Param("accountId") String accountId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query(value = "select c.id, CONVERT(DATETIME2, max(ls.viewed_at), 1) as lastActivity, c.name, STRING_AGG(la.name, ', ') AS linkAccountNames from contact c " +
            "left join link_statistic as ls on ls.contact_id = c.id " +
            "join link as l on l.id = ls.link_id " +
            "join link_accounts as la on la.id = l.link_accounts_id " +
            "where c.company_id = :companyId " +
            "and (c.name like concat('%', :keyword, '%') or c.email like concat('%', :keyword, '%'))" +
            "group by c.id, c.name ",
            nativeQuery = true,
            countQuery = "select count(c.id) from contact c " +
                    " where c.name like concat('%', :keyword, '%') and c.company_id = :companyId ")
    Page<ContactSearchInfo> searchContact(String keyword, String companyId, Pageable pageable);
}
