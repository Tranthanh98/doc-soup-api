package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "subscription")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SubscriptionEntity extends BaseAuditEntity {
    @Id
    @Type(type = "uuid-char")
    UUID companyId;

    @Column(length = 36)
    String accountId;

    String subType;

    @Column(nullable = false)
    Long planTierId;

    @Column(nullable = false, columnDefinition = "nvarchar(200)")
    String paypalPlanId;

    @Column(columnDefinition = "nvarchar(200)" )
    String subscriptionPaypalId;

    @Column(columnDefinition = "nvarchar(max)")
    String paypalSubscriptionPayload;

    @Column(nullable = true,columnDefinition = "nvarchar(400)" )
    String notes;

}
