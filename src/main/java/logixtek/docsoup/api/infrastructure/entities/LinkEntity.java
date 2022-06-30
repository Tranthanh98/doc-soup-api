package logixtek.docsoup.api.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "link", indexes = {
        @Index(name = "IX_LINK_REFID", columnList = "refId", unique = false),
        @Index(name = "IX_LINK_DOCUMENTID", columnList = "documentId", unique = false),
        @Index(name = "IX_LINK_PARENT_REFID", columnList = "refId,parent", unique = false),
        @Index(name = "IX_LINK_CREATED_BY_COMPANY_ID",  columnList="createdBy,companyId"),
        @Index(name = "IX_LINK_LINK_ACCOUNTS_ID", columnList = "linkAccountsId"),
        @Index(name = "IX_LINK_COMPANY_ID_STATUS",  columnList="companyId,status"),
})
public class LinkEntity extends BaseAuditEntity{
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    UUID id;


    @JsonIgnore
    @Column(nullable = false)
    Long refId;

    @JsonIgnore
    @Type(type = "uuid-char")
    UUID documentId;


    @JsonIgnore
    @Column(nullable = false)
    long visit = 0;

    @Column(columnDefinition = "nvarchar(4000)")
    String secure;

    @Column(nullable = false)
    Boolean download =false;

    Long watermarkId;

    @Column(nullable = true)
    OffsetDateTime expiredAt;

    @JsonIgnore
    @Type(type = "uuid-char")
    UUID parent;

    // 0: active, 1: disabled, 2: deActive
    @Column(nullable = false)
    Integer status = LinkConstant.ACTIVE_STATUS;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    Long ndaId;

    @Column(nullable = false)
    Long linkAccountsId;
}
