package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "account", indexes = {
        @Index(name = "IX_ACCOUNT_EMAIL", columnList = "email", unique = true)
})
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class AccountEntity extends BaseAuditEntity{
    @Id
    @Column(length = 36)
    String id;

    @Column(length = 50,columnDefinition = "nvarchar(50)")
    String firstName;

    @Column(length = 50,columnDefinition = "nvarchar(50)")
    String lastName;

    @Column(length = 200,nullable = false,columnDefinition = "nvarchar(200)")
    String email;

    @Column(length = 20)
    String phone;

    Instant checkInTime;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID activeCompanyId;

    @Column(columnDefinition = "varchar(max)")
    String token;

    @Column(nullable = false)
    Boolean enable = true;

    @Column(nullable = false)
    Boolean sendDailySummary = true;

    @Column(nullable = false)
    Boolean sendWeeklySummary = true;
}
