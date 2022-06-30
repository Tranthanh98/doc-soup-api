package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "company_user",
        indexes = {
                @Index(name = "IX_COMPANY_USER_EMAIL",  columnList="email", unique = false),
                @Index(name = "IX_COMPANY_USER_COMPANYID", columnList="companyId", unique = false),
                @Index(name = "IX_COMPANY_USER_ACCOUNTID", columnList="accountId", unique = false),
                @Index(name = "IX_COMPANY_USER_TOKEN", columnList="token", unique = false)
        })
@Entity
@NamedStoredProcedureQuery(name = "CompanyUserEntity.transferDataToAnotherUser", procedureName = "transfer_data_to_another_user", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "sourceAccountId", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "destinationAccountId", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "companyId", type = String.class),
})
@NamedStoredProcedureQuery(name = "CompanyUserEntity.deactivateAllLinkByAccountIdAndCompanyId", procedureName = "deactive_all_link_by_account_id_and_company_id", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "accountId", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "companyId", type = String.class),
})
@NamedStoredProcedureQuery(name = "CompanyUserEntity.reactiveAllLinkByAccountIdAndCompanyId", procedureName = "reactive_all_link_by_account_id_and_company_id", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "accountId", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "companyId", type = String.class),
})
@Data
@EqualsAndHashCode(callSuper=false)
public class CompanyUserEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false,length = 200,columnDefinition = "nvarchar(200)")
    String email;

    @Column(length = 36)
    String accountId;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    @Column(nullable = false)
    // 0:owner, 1:invited
    Integer member_type = 0;

    @Column(nullable = false)
    //1:active, 0:deActive ,-1 :Suspended, -2: transferred
    Integer status = 1;

    @Column(nullable = false, length = 50)
    //c_admin, c_member
    String role;

    String token;

    // 0: invited, 1: AcceptedInvitation, -1: RejectInvitation
    @Column(nullable = false)
    Integer invitationStatus = 0;

    OffsetDateTime rejectDate;
}