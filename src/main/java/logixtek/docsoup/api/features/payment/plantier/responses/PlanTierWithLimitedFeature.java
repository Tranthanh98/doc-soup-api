package logixtek.docsoup.api.features.payment.plantier.responses;

import logixtek.docsoup.api.infrastructure.models.FeatureFlat;
import lombok.Data;

import java.util.List;

@Data
public class PlanTierWithLimitedFeature {

    Long id;

    String name;

    Double initialFee;

    Integer initialSeat;

    Double seatPrice;

    Long level;

    List<FeatureFlat> limits;

    Double yearlyDiscount;

    String monthlyPlanPaypalId;

    String yearlyPlanPaypalId;

    String monthlyFixedPlanPaypalId;

    String yearlyFixedPlanPaypalId;
}
