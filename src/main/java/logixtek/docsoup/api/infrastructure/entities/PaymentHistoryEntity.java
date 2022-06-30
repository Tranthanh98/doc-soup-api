package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "payment_history")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PaymentHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    @Column(nullable = false)
    Integer quantity;

    @Column(nullable = false, columnDefinition = "decimal")
    Double price;

    @Column(nullable = false)
    OffsetDateTime createdDate;

    String status;

    @Column(nullable = false, columnDefinition = "decimal")
    Double totalAmount;

    @Column(nullable = false, columnDefinition = "decimal")
    Double subTotalAmount;

    String currency;

    @Column(columnDefinition = "nvarchar(200)" )
    String subscriptionPaypalId;

    @Column(nullable = false, columnDefinition = "nvarchar(200)")
    String paypalPlanId;

    @Column(columnDefinition = "nvarchar(MAX)" )
    String payPalPaymentPayload;

    @Column(nullable = false, columnDefinition = "decimal")
    Double initialFee = 0.0;

    Double discount = 0.0;

    @Column(nullable = false)
    Boolean sentInvoice = false;

    @Column(columnDefinition = "nvarchar(500)" )
    String invoice;
}
