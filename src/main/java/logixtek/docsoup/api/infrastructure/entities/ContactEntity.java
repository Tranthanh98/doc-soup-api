package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;


@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact", indexes = {
        @Index(name = "IX_CONTACT_ENTITY_ACCOUNTID", columnList = "accountId", unique = false),
        @Index(name = "IX_CONTACT_ENTITY_EMAIL", columnList = "email", unique = false),
        @Index(name = "IX_CONTACT_ENTITY_COMPANYID", columnList = "companyId", unique = false),
        @Index(name = "IX_CONTACT_ENTITY_ACCOUNT_ID_COMPANY_ID",  columnList="accountId,companyId"),
        @Index(name = "IX_CONTACT_ENTITY_CREATED_BY_COMPANY_ID",  columnList="createdBy,companyId"),
        @Index(name = "IX_CONTACT_ENTITY_MODIFIED_BY_COMPANY_ID",  columnList="modifiedBy,companyId")
})
public class ContactEntity extends BaseAuditEntity implements OwnerInfo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 36)
    String accountId;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    @Column(nullable = false)
    String email;

    @Column(nullable = true, columnDefinition = "nvarchar(255)")
    String name;

    @Column(nullable = false)
    Boolean archived =false;
}
