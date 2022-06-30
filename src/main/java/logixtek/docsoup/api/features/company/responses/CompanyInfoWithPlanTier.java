package logixtek.docsoup.api.features.company.responses;

import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CompanyInfoWithPlanTier {
    private UUID id;

    private String name;

    private Boolean trackingOwnerVisit;

    private Long planTierId;

    private String billingContact;

    private String billingInfoName;

    private String billingInfoStreet;

    private String billingInfoCity;

    private String billingInfoState;

    private String billingInfoZipCode;

    private String billingInfoTaxId;

    private Integer totalUsers;

    private PlanTierEntity planTier;

    private String currentSubscriptionType;

    private Integer totalLinks;

    private Long totalVisits;
}
