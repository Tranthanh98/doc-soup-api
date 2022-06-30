package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Data
@Table(name = "subscription_history")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SubscriptionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 36)
    String accountId;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    String subType;

    @Column(nullable = false)
    Long planTierId;

    @Column(nullable = true,columnDefinition = "nvarchar(200)" )
    String paypalPlanId;

    @Column(nullable = true,columnDefinition = "nvarchar(200)" )
    String subscriptionPaypalId;

    @Column(columnDefinition = "nvarchar(max)")
    String paypalSubscriptionPayload;
    
    @Column(nullable = true,columnDefinition = "nvarchar(400)" )
    String notes;

    @Column(nullable = false)
     OffsetDateTime createdDate;

     OffsetDateTime modifiedDate;

    @Column(length = 36,nullable = false)
     String createdBy;

    @Column(length = 36)
     String modifiedBy;

    @Column(nullable = false)
    OffsetDateTime historyDate;

    @PrePersist
    protected void onCreated(){
        historyDate = Instant.now().atOffset(ZoneOffset.UTC);
    }

}
