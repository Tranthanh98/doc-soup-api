package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import logixtek.docsoup.api.infrastructure.models.RoomContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataRoomContentRepository extends JpaRepository<DataRoomContentEntity, Long> {

    @Query("SELECT dir, f, ct from DataRoomEntity d"
    + " INNER JOIN DataRoomContentEntity ct"
    + " ON d.id = ct.dataRoomId"
    + " LEFT JOIN DirectoryEntity dir"
    + " on ct.directoryId = dir.id"
    + " LEFT JOIN FileEntity f"
    + " on ct.fileId = f.id"
    + " WHERE d.isActive= true and d.id=:dataRoomId")
    Optional<List<Object[]>> findAllByDataRoomId(@Param("dataRoomId")  long dataRoomId);

    Optional<DataRoomContentEntity> findByDataRoomIdAndDirectoryId(Long dataRoomId, Long directoryId);

    Optional<DataRoomContentEntity> findByDataRoomIdAndFileId(Long dataRoomId, Long fileId);

    @Transactional
    @Modifying
    @Query(value = "insert into data_room_content(data_room_id, created_by, created_date, directory_id, file_id, modified_by, modified_date)" +
            " select :newDataRoomId as data_room_id, created_by, created_date, directory_id, file_id, modified_by, modified_date from data_room_content" +
            " WHERE data_room_id = :oldDataRoomId", nativeQuery = true)
    void duplicateDataRoomContent(@Param("oldDataRoomId") Long oldDataRoomId, @Param("newDataRoomId") Long newDataRoomId);


    @Query("select dc.id as contentId,de.id as id,de.name as name ,de.name as displayName from DataRoomContentEntity dc "
            + "inner join DirectoryEntity de "
            + "on dc.directoryId = de.id "
            + "where dc.dataRoomId=:dataRoomId and dc.isActive = true")
    Optional<List<RoomContent>> findAllDirectoryByDataRoomIdAndIsActiveIsTrue(@Param("dataRoomId") long dataRoomId);

    @Query("select dc.id as contentId,fe.id as id,fe.name as name,fe.displayName from DataRoomContentEntity dc "
            + "inner join FileEntity fe "
            + "on dc.fileId = fe.id "
            + "where dc.dataRoomId=:dataRoomId and dc.isActive = true")
    Optional<List<RoomContent>> findAllFileByDataRoomIdAndIsActiveIsTrue(@Param("dataRoomId") long dataRoomId);

    @Modifying
    @Transactional
    @Query(value = "EXECUTE [dbo].[upd_increase_order_no_dataroom_content] :contentId, :afterId", nativeQuery = true)
    void updateIncreaseOrderNo(@Param("contentId") Long contentId, @Param("afterId") Long afterId);

    @Modifying
    @Transactional
    @Query(value = "EXECUTE [dbo].[upd_decrease_order_no_dataroom_content] :contentId, :beforeId", nativeQuery = true)
    void updateDecreaseOrderNo(@Param("contentId") Long contentId,@Param("beforeId") Long beforeId);

    Optional<DataRoomContentEntity> findFirstByDataRoomIdOrderByOrderNoDesc(Long dataRoomId);

    @Query(value = "with temp(directory_id) as (" +
            "    select d1.directory_id" +
            "    from data_room_content as d1 with(nolock)" +
            "    where data_room_id = :dataRoomId and directory_id is not null" +
            "    UNION ALL" +
            "    select d.id" +
            "    from directory_entity as d with(nolock), temp as c" +
            "    where d.parent_id = c.directory_id) " +
            "select count(f.id) from file_entity as f with(nolock) " +
            " join temp as t on t.directory_id = f.directory_id ", nativeQuery = true)
    Long countAllContentByDataRoomId(@Param("dataRoomId") Long dataRoomId);

    @Query(value = "with temp(directory_id) as (  " +
            "    select dr.directory_id " +
            "    from data_room_content as dr with(nolock)   " +
            "    where data_room_id in (   " +
            "        select d1.id from data_room as d1 with(nolock) " +
            "        where d1.company_id = :companyId " +
            "    ) and dr.directory_id is not null   " +
            "       UNION ALL   " +
            "    select d.id  from directory_entity as d with(nolock), temp as c " +
            "    where d.parent_id = c.directory_id " +
            ")" +
            "SELECT SUM(m.TOTAL) FROM (" +
            "    select count(f.id) AS TOTAL from file_entity as f with(nolock) " +
            "    join temp as t on f.directory_id = t.directory_id " +
            "        union ALL " +
            "    select COUNT(dr.file_id) AS TOTAL " +
            "    from data_room_content as dr with(nolock)   " +
            "    where dr.file_id is not null and data_room_id in (       " +
            "        select d1.id from data_room as d1 with(nolock)" +
            "        where d1.company_id = :companyId " +
            "    )" +
            ") AS m " , nativeQuery = true)
    Long countAllDataRoomContentByCompanyId(@Param("companyId") String companyId);

    @Query("select max(orderNo) from DataRoomContentEntity " +
            "where dataRoomId = :dataRoomId")
    Long getMaxOrderNoByDataRoomId(@Param("dataRoomId") Long dataRoomId);
}
