package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.models.ActivityWithLinkAndVisit;
import logixtek.docsoup.api.infrastructure.models.DocumentInfoWithLinkSetting;
import logixtek.docsoup.api.infrastructure.models.LinkAndDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    Optional<List<DocumentEntity>> findAllByRefId(UUID refId);

    @Query("select doc from DocumentEntity doc "+
            "inner join LinkEntity sl "+
            "on sl.documentId = doc.id "+
            "inner join LinkEntity l "+
            "on sl.parent = l.id "+
            "inner join DataRoomEntity d "+
            "on d.id = l.refId and l.documentId is NULL "+
            "where d.id=:dataRoomId"
             )
    Optional<List<DocumentEntity>> findAllByDataRoomId(@Param("dataRoomId") Long dataRoomId);

    @Query("select doc from DocumentEntity doc "+
            "inner join LinkEntity l "+
            "on l.documentId = doc.id "+
            "inner join FileEntity f "+
            "on f.id = l.refId and l.documentId is NOT NULL "+
            "where f.id=:fileId"
    )
    Optional<List<DocumentEntity>> findAllByFileId(@Param("fileId") Long fileId);

    @Query("select doc.id as id, l.download as download, l.expiredAt as expiredAt, doc.expiredAt as lifeSpan from DocumentEntity doc "+
            "inner join LinkEntity l "+
            "on l.documentId = doc.id "+
            "inner join FileEntity f "+
            "on f.id = l.refId and l.documentId is NOT NULL "+
            "where f.id=:fileId"
    )
    Optional<List<DocumentInfoWithLinkSetting>> findAllDocumentWithSettingValueByFileId(@Param("fileId") Long fileId);

    @Query(value = "EXECUTE [dbo].[sel_documents_and_links] :groupBy, :startDate, :endDate", nativeQuery = true)
    Collection<LinkAndDocuments> getDocumentsAndLinks(@Param("groupBy") String groupBy,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    @Query(value = "EXECUTE [dbo].[sel_activities] :groupBy, :startDate, :endDate", nativeQuery = true)
    Collection<ActivityWithLinkAndVisit> getActivities(@Param("groupBy") String groupBy,
                                                       @Param("startDate")LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    Optional<DocumentEntity> findFirstByFileIdAndFileVersionAndRefIdIsNull(Long fileId, Integer fileVersion);

    @Query("select d from DocumentEntity as d " +
            "join FileEntity as f on f.id = d.fileId " +
            "where d.fileVersion = (select version from FileEntity where id = :fileId)")
    Optional<DocumentEntity> findByFileIdWithVersion(Long fileId);
}