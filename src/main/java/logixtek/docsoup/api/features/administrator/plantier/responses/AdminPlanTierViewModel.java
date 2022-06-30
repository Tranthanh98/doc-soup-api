package logixtek.docsoup.api.features.administrator.plantier.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminPlanTierViewModel {
    private Long id;

    private String name;

    private Long level;

    private Double initialFee;

    private Integer initialSeat;

    private Double seatPrice;

    private Double yearlyDiscount;

    private String description;

    private Boolean isActive;
}
