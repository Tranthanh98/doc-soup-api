package logixtek.docsoup.api.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import logixtek.docsoup.api.infrastructure.models.SummaryStatisticOnFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "IX_FILE_ENTITY_COMPANYID_ACCOUNTID", columnList="companyId,accountId", unique = false),
        @Index(name = "IX_FILE_ENTITY_COMPANYID_DIRECTORYID",columnList = "companyId,directoryId", unique = false),
        @Index(name = "IX_FILE_ENTITY_CREATED_BY_COMPANY_ID",  columnList="createdBy,companyId"),
        @Index(name = "IX_FILE_ENTITY_MODIFIED_BY_COMPANY_ID",  columnList="modifiedBy,companyId"),
        @Index(name = "IX_FILE_ENTITY_COMPANY_ID_DISPLAY_NAME", columnList = "companyId, displayName")
})
@NamedStoredProcedureQuery(name = "FileEntity.sel_summary_statistic_on_file",
        resultClasses = SummaryStatisticOnFile.class,
        procedureName = "sel_summary_statistic_on_file", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "fileId", type = Long.class),

})
@NamedStoredProcedureQuery(name = "FileEntity.sel_page_stats",
        procedureName = "sel_page_stats", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "fileId", type = Long.class),

})
@NamedStoredProcedureQuery(name = "FileEntity.delete", procedureName = "del_file_entity", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "id", type = Long.class),
})
public class FileEntity extends BaseAuditEntity implements OwnerInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long directoryId;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    String displayName;

    @Column(nullable = false)
    long size;

    @Column(nullable = false, length = 10)
    String extension;

    @Column(nullable = false)
    Boolean nda = false;

    @Column(length = 36,nullable = false)
    String accountId;

    @JsonIgnore
    OffsetDateTime docExpiredAt;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    @Column(nullable = false)
    Integer version = 1;
}
