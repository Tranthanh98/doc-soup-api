package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Table(name = "data_room_user",
        indexes = {
                @Index(name = "IX_DATA_ROOM_USER_DATAROOMID", columnList="dataRoomId", unique = false),
                @Index(name = "IX_DATA_ROOM_USER_USERID", columnList="userId", unique = false),
                @Index(name = "IX_DATA_ROOM_USER_CREATED_BY",  columnList="createdBy")
        })
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class DataRoomUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long dataRoomId;

    @Column(length = 36)
    String userId;

    @Column(nullable = false)
    OffsetDateTime createdDate = OffsetDateTime.now(ZoneOffset.UTC);

    @Column(length = 36,nullable = false)
    String createdBy;
}