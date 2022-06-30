package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "company")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CompanyEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    UUID id;

    @Column(nullable = false,columnDefinition = "nvarchar(200)")
    String name;

    @Column(nullable = false)
    Boolean trackingOwnerVisit = true;

    @Column(nullable = false)
    Long planTierId;

    @Column(columnDefinition = "nvarchar(150)")
    String billingContact;

    @Column(columnDefinition = "nvarchar(150)")
    String billingInfoName;

    @Column(columnDefinition = "nvarchar(150)")
    String billingInfoStreet;

    @Column(columnDefinition = "nvarchar(150)")
    String billingInfoCity;

    @Column(columnDefinition = "nvarchar(150)")
    String billingInfoState;

    @Column(columnDefinition = "nvarchar(25)")
    String billingInfoZipCode;

    @Column(columnDefinition = "nvarchar(25)")
    String billingInfoTaxId;
}