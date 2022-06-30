package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.DataRoomUserEntity;
import logixtek.docsoup.api.infrastructure.models.DataRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataRoomUserRepository extends JpaRepository<DataRoomUserEntity, Long> {
    Optional<Collection<DataRoomUserEntity>> findAllByDataRoomId(@Param("companyId") UUID companyId);

    Optional<DataRoomUserEntity> findByUserIdAndDataRoomId(@Param("userId") String userId, @Param("dataRoomId") Long dataRoomId);

    @Query("select du.dataRoomId as dataRoomId, du.userId as userId, concat(ac.firstName,' ',ac.lastName) as fullName, ac.email as email " +
            "from DataRoomUserEntity du  " +
            "inner join AccountEntity ac " +
            "on ac.id = du.userId " +
            "where du.dataRoomId = :dataRoomId"
    )
    Optional<List<DataRoomUser>> findAllUserOfDataRoom(@Param("dataRoomId") Long dataRoomId);
}
