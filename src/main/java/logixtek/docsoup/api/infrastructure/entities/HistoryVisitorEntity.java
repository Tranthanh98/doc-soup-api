package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;


@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history_visitor")
public class HistoryVisitorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Type(type = "uuid-char")
    @Column(nullable = false)
    UUID linkId;

    @Column(nullable = false)
    Long viewerId;

    @Column(nullable = false)
    OffsetDateTime timestamp;

    @Column(nullable = false)
    String action;

    @Column(nullable = false)
    String userAgent;
}
