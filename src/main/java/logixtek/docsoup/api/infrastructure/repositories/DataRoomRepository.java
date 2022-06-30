package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.DataRoomEntity;
import logixtek.docsoup.api.infrastructure.models.DataRoomInfo;
import logixtek.docsoup.api.infrastructure.models.FileEntityWithVisits;
import logixtek.docsoup.api.infrastructure.models.SimplifiedDataRoomInfo;
import logixtek.docsoup.api.infrastructure.models.FileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.*;

public interface DataRoomRepository extends JpaRepository<DataRoomEntity, Long> {

    @Query(value = "EXECUTE [dbo].[sel_data_room_by_account_id_and_company_id] :accountId, :companyId", nativeQuery = true)
    Optional<List<DataRoomInfo>> findAllDataRoomsByAccountIdAndCompanyId(@Param("accountId")  String accountId, @Param("companyId") String companyId);

    Optional<DataRoomEntity> findByIdAndAccountIdAndCompanyId(Long id, String accountId,UUID companyId);

    @Query(value = "EXECUTE [dbo].[sel_invited_data_room_by_user_id_and_company_id] :userId, :companyId", nativeQuery = true)
    Optional<List<DataRoomInfo>> findAllInvitedDataRoomsByUserIdAndCompanyId(@Param("userId") String userId, @Param("companyId") String companyId);

    @Procedure(name = "DataRoomEntity.delete")
    void deleteAllById(@Param("id") Long id);

    @Query(value = "EXECUTE [dbo].[get_all_file_of_data_room_content] :dataRoomId", nativeQuery = true)
    Optional<List<FileInfo>> getAllFileOfDataRoomContent(@Param("dataRoomId") Long dataRoomId);

    @Query("SELECT d.id AS id, d.name AS name, d.createdDate AS createdDate, CONCAT(a.firstName, ' ', a.lastName) as owner " +
            "FROM DataRoomEntity AS d " +
            "INNER JOIN AccountEntity a ON d.accountId = a.id " +
            "LEFT JOIN DataRoomUserEntity AS dru ON dru.dataRoomId = d.id " +
            "LEFT JOIN AccountEntity as ac ON ac.id = dru.userId " +
            "WHERE d.name LIKE CONCAT('%', :keyword, '%') AND d.companyId = :companyId " +
            "AND d.isActive = TRUE AND (d.accountId = :accountId OR dru.userId = :accountId) "
    )
    Page<SimplifiedDataRoomInfo> searchDataRoom(String keyword, UUID companyId, String accountId, Pageable pageable);

    @Query("select d.id as id, d.name as name, count(l.id) as links " +
            "from LinkEntity l " +
            "inner join DataRoomEntity d on d.id = l.refId " +
            "where (cast(l.createdDate as date) BETWEEN cast(:fromDate as date) AND cast(:toDate as date)) and d.companyId=:companyId and l.documentId is null " +
            "group by d.id, d.name " +
            "order by count(l.id) DESC")
    Optional<Collection<SimplifiedDataRoomInfo>> findAllDataRoomWithLinkByCompanyIdAndLinkCreatedBetweenDateOrderByLinks(UUID companyId, OffsetDateTime fromDate, OffsetDateTime toDate);

}
