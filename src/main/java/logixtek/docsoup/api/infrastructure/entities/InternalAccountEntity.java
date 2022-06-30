package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Table(name = "internal_account", indexes = {
        @Index(name = "IX_ACCOUNT_EMAIL", columnList = "email", unique = true)
})
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class InternalAccountEntity extends BaseAuditEntity {
    @Id
    @Column(length = 36)
    String id;

    @Column(length = 200,nullable = false,columnDefinition = "nvarchar(200)")
    String email;

    @Column(nullable = false)
    Instant checkInTime;
}
