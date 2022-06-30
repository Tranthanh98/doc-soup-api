package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_room",
        indexes = {
                @Index(name = "IX_DATA_ROOM_ACCOUNT_ID_COMPANY_ID",  columnList="accountId,companyId"),
                @Index(name = "IX_DATA_ROOM_CREATED_BY_COMPANY_ID",  columnList="createdBy,companyId"),
                @Index(name = "IX_DATA_ROOM_MODIFIED_BY_COMPANY_ID",  columnList="modifiedBy,companyId"),
                @Index(name = "IX_DATA_ROOM_NAME_COMPANY_ID",  columnList="name,companyId")
        })
@NamedStoredProcedureQuery(name = "DataRoomEntity.delete", procedureName = "del_data_room", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "id", type = Long.class),
})

@NamedStoredProcedureQuery(name = "DataRoomEntity.getAllFile", procedureName = "get_all_file_of_data_room_content", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "dataRoomId", type = Long.class)
})
public class DataRoomEntity extends BaseAuditEntity implements OwnerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    String name;

    @Column(nullable = false)
    Boolean isActive;

    @Column(length = 36,nullable = false)
    String accountId;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    @Column(nullable = false)
    // 0: list, 1: grid
    Integer viewType = 0;
}
