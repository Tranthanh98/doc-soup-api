package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface BillingInfoWithPlanName {
    Long getId();

    String getAccountId();

    UUID getCompanyId();

    Integer getSeat();

    Double getPrice();

    Double getPriceAfterDiscount();

    Integer getStatus();

    Double getTotalAmount();

    OffsetDateTime getNextBill();

    String getSubType();

    String getNotes();

    Long getPlanTierId();

    String getPlanTierName();

    Double getDiscount();

    Integer getProcessStatus();

    Double getInitialFee();

    Double getInitialFeeAfterDiscount();

    Integer getInitialSeat();

    OffsetDateTime getCreatedDate();

    OffsetDateTime getModifiedDate();
}
