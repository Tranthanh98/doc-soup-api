package logixtek.docsoup.api.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Blob;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "watermark", indexes = {
        @Index(name="IX_WATERMARK_COMPANYID_ACCOUNTID", columnList = "companyId,accountId", unique = false),
        @Index(name = "IX_WATERMARK_CREATED_BY_COMPANY_ID",  columnList="createdBy,companyId"),
        @Index(name = "IX_WATERMARK_MODIFIED_BY_COMPANY_ID",  columnList="modifiedBy,companyId")
})
public class WatermarkEntity extends BaseAuditEntity implements OwnerInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(columnDefinition="nvarchar(4000)",length = 4000, nullable = false)
    String text;

    @Column(nullable = true, columnDefinition="BLOB")
    @Lob
    @JsonIgnore
    Blob  image;

    @Column(length = 15)
    String imageType;

    @Column(nullable = false)
    Boolean isDefault = false;

    @JsonIgnore
    @Column(length = 36,nullable = false)
    String accountId;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;
}
