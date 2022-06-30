package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "plan_tier")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PlanTierEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String name;

    @Column(nullable = false)
    private Long level;

    @Column(nullable = false, columnDefinition = "decimal")
    private Double initialFee;

    private Integer initialSeat;

    @Column(columnDefinition = "decimal")
    private Double seatPrice;

    private Double yearlyDiscount;

    @Column( columnDefinition = "nvarchar(200)")
    private String monthlyPlanPaypalId;

    @Column( columnDefinition = "nvarchar(200)")
    private String yearlyPlanPaypalId;

    @Column( columnDefinition = "nvarchar(200)")
    private String monthlyFixedPlanPaypalId;

    @Column( columnDefinition = "nvarchar(200)")
    private String yearlyFixedPlanPaypalId;

    @Column(columnDefinition = "nvarchar(255)")
    private String description;

    @Column(nullable = false)
    private Boolean isActive;
}
