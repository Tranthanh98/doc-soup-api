package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "link_accounts", indexes = {
        @Index(name = "IX_LINK_ACCOUNTS_NAME_COMPANY_ID",  columnList="name,companyId")
})
@NamedStoredProcedureQuery(name = "LinkAccountsEntity.mergeLinkAccount", procedureName = "merge_link_account", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "sourceLinkAccountId", type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "destinationLinkAccountId", type = Long.class),
})
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class LinkAccountsEntity extends BaseAuditEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    String name;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    Boolean archived = false;
}
