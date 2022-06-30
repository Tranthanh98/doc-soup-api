package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "feature_flag")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FeatureFlagEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String featureKey;

    @Column(nullable = false)
    Long planTierId;

    Integer limit;

    String unit;
}
