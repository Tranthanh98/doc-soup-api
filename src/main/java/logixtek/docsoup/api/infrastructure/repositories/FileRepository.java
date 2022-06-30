package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<List<FileEntity>> findAllByDirectoryIdAndCompanyId(long directoryId, UUID companyId);

    @Query("select f.id as id, f.displayName as displayName, f.directoryId as directoryId, f.size as size, f.accountId as accountId, f.extension as extension, concat(a.firstName, a.lastName) as ownerName, " +
            "f.nda as nda, f.companyId as companyId, f.modifiedDate as modifiedDate, f.createdDate as createdDate, sum(s.visit) as recentVisits, f.version as version " +
            "from FileEntity f " +
            "LEFT JOIN LinkEntity l " +
            "on f.id = l.refId and l.documentId is not NULL " +
            "LEFT JOIN LinkStatisticEntity s "+
            "on l.id= s.linkId " +
            "left join AccountEntity as a on a.id = f.createdBy "+
            "where f.directoryId = :directoryId and f.companyId = :companyId "+
            "GROUP BY f.id, f.displayName, f.directoryId, f.size, f.accountId, f.extension, f.nda, f.companyId, f.modifiedDate, f.createdDate, a.firstName, a.lastName, f.version ")
    Optional<List<FileEntityWithVisits>> findAllFileWithVisitsByDirectoryIdAndCompanyId(long directoryId, UUID companyId);

    Optional<FileEntity> findByDisplayNameAndDirectoryIdAndCompanyId(String displayName, long directoryId, UUID companyId);

    Optional<List<FileEntity>> findAllByAccountIdAndCompanyIdAndNdaIsTrue(String accountId, UUID companyId);

    @Query("select f.id as fileId,f.displayName  as fileDisplayName, f.version as version, count(s.id) as visits,SUM(s.duration) as timeSpent,MAX(s.viewedAt) as lastActivity " +
            "from FileEntity f "+
            "INNER JOIN LinkEntity l " +
            "on f.id = l.refId and l.documentId is not NULL " +
            "INNER JOIN LinkStatisticEntity s "+
            "on l.id= s.linkId and s.contactId is not NULL " +
            "where s.contactId = :contactId "+
            "GROUP BY f.id, f.version, f.displayName, s.contactId")
    Optional<Collection<FileStatisticOnContact>> findAllFileByContactId(@Param("contactId") Long contactId);


    @Query(value = "EXECUTE [dbo].[sel_summary_statistic_on_file] :fileId", nativeQuery = true)
    Optional<List<SummaryStatisticOnFile>> findSummaryStatisticByFileId(@Param("fileId") Long fileId);

    @Query(value = "EXECUTE [dbo].[sel_page_stats] :fileId, :version", nativeQuery = true)
    Optional<Collection<PageStats>> findAllPageStatsByFileIdAndVersion(@Param("fileId") Long fileId, @Param("version") Integer version);

    @Procedure(name = "FileEntity.delete")
    void deleteAllById(@Param("id") Long id);

    Optional<FileEntity> findByIdAndCompanyIdAndNdaIsTrue(Long ndaId, UUID companyId);

    @Query(value = "EXECUTE [dbo].[sel_document_activity] :userId, :companyId, :top, :sortDirection, :dateRecent", nativeQuery = true)
    Optional<List<DocumentActivity>> findDocumentActivityByUserIdAndCompanyId(String userId, String companyId, Integer top, String sortDirection, int dateRecent);

    @Query(value = "EXECUTE [dbo].[sel_file_by_keyword] :keyword, :companyId, :accountId, :page, :pageSize", nativeQuery = true)
    Optional<Collection<ContentResult>> findFileWithKeyword(@Param("keyword") String keyword, @Param("companyId") String companyId, @Param("accountId") String accountId, @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    int countAllByCompanyId(UUID companyId);

    @Query("select sum(f.size) from FileEntity f where f.companyId = :companyId and f.accountId = :accountId")
    Long sumAllSizeByCompanyIdAndAccountId(UUID companyId, String accountId);

    Optional<FileEntity> findFirstByAccountIdAndCompanyIdOrderByCreatedDateDesc(String accountId, UUID companyId);

    @Query(value = "EXECUTE [dbo].[sel_user_statistic_of_company] :accountId, :companyId, :numOfDay ", nativeQuery = true)
    Optional<Collection<UserStatisticOfFile>> getListFileWithLinksAndDataRoomsByAccountId(@Param("accountId") String accountId,
                                                                                          @Param("companyId") String companyId,
                                                                                          @Param("numOfDay") Integer numOfDay);

    @Query("select f.id as id, f.displayName as displayName, count(l.id) as links " +
            "from LinkEntity l " +
            "inner join FileEntity f on f.id = l.refId " +
            "where (cast(l.createdDate as date) BETWEEN cast(:fromDate as date) AND cast(:toDate as date)) and f.companyId=:companyId and l.documentId is not null " +
            "group by f.id, f.displayName " +
            "order by count(l.id) DESC")
    Optional<Collection<FileEntityWithVisits>> findAllFileWithLinkByCompanyIdAndLinkCreatedBetweenDateOrderByLinks(UUID companyId, OffsetDateTime fromDate, OffsetDateTime toDate);

    @Query("select f.id as fileId,f.displayName as displayName, sum(s.visit) as recentVisits " +
            "from FileEntity f "+
            "INNER JOIN LinkEntity l " +
            "on f.id = l.refId and l.documentId is not NULL " +
            "INNER JOIN LinkStatisticEntity s "+
            "on l.id= s.linkId " +
            "where (cast(l.createdDate as date) BETWEEN cast(:fromDate as date) AND cast(:toDate as date)) and f.companyId=:companyId and l.documentId is not null "+
            "GROUP BY f.id, f.displayName " +
            "ORDER BY sum(s.visit) DESC" )
    Optional<Collection<FileEntityWithVisits>> findAllFileWithVisitByCompanyIdAndLinkCreatedBetweenDateOrderByVisits(UUID companyId, OffsetDateTime fromDate, OffsetDateTime toDate);

    @Query("select  f.id as fileId, f.displayName as displayName, concat(a.firstName, ' ', a.lastName) as ownerName " +
            "from FileEntity f " +
            "inner join AccountEntity a on a.id=f.createdBy " +
            "where (cast(f.createdDate as date) BETWEEN cast(:fromDate as date) AND cast(:toDate as date)) and f.companyId=:companyId")
    Optional<Collection<FileEntityWithVisits>> findAllByCompanyIdAndCreatedDateBetween(UUID companyId, OffsetDateTime fromDate, OffsetDateTime toDate);

    @Query("select version from FileEntity where id = :fileId")
    Integer getVersionByFileId(Long fileId);
}
