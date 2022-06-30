package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "data_room_content", indexes = {
    @Index(name="IX_DATA_ROOM_CONTENT_DATAROOMID", columnList = "dataRoomId", unique = false),
        @Index(name = "IX_DATA_ROOM_CONTENT_CREATED_BY",  columnList="createdBy"),
        @Index(name = "IX_DATA_ROOM_CONTENT_MODIFIED_BY_BY",  columnList="modifiedBy")
})
public class DataRoomContentEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long dataRoomId;

    @Column(nullable = true)
    Long directoryId;

    @Column(nullable = true)
    Long fileId;

    @Column(nullable = false)
    Integer orderNo = 0;
    
    Boolean isActive = true;
}
