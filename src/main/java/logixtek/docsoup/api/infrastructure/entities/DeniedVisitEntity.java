package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Data
@Table(name = "denied_visit")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class DeniedVisitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    UUID linkId;

    OffsetDateTime visitTime = OffsetDateTime.now(ZoneOffset.UTC);

    String email;

    Boolean sentEmail = false;
}
