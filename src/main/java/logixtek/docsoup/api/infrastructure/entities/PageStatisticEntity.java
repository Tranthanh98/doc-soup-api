package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "page_statistic",indexes = {
        @Index(name = "IX_PAGE_STATISTIC_LINKSTATISTICID", columnList = "linkStatisticId"),
        @Index(name = "IX_PAGE_STATISTIC_LINKID", columnList = "linkId"),
        @Index(name = "IX_PAGE_STATISTIC_LINKSTATISTICID_PAGE", columnList = "linkStatisticId,page")
})
public class PageStatisticEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Type(type = "uuid-char")
    UUID linkId;

    Long linkStatisticId;

    @Column(nullable = false)
    private int page = 1;

    @Column(nullable = false)
    private int visit =0;

    @Column(nullable = false)
    private long duration = 0;

    @Column(length = 36)
    private String sessionId;

    @Column(nullable = false)
    protected OffsetDateTime modifiedDate = OffsetDateTime.now(ZoneOffset.UTC);

    @Column(nullable = false)
    Integer version = 1;
}
