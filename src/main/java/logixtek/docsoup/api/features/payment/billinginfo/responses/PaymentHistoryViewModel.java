package logixtek.docsoup.api.features.payment.billinginfo.responses;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentHistoryViewModel {
    Long id;

    UUID companyId;

    Integer quantity;

    Double price;

    OffsetDateTime createdDate;

    String status;

    Double totalAmount;

    Double subTotalAmount;

    String currency;

    String payPalPaymentPayload;

    Double initialFee;

    Double initialSeat;

    Double discount;

    Boolean sentInvoice;

    String invoice;
}
