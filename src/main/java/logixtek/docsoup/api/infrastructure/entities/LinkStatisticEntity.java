package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="link_statistic", indexes = {
        @Index(name = "IX_LINK_STATIC_LINKID", columnList = "linkId"),
        @Index(name = "IX_LINK_STATISTIC_LINKID_DEVICEID",columnList = "linkId,deviceId"),
        @Index(name = "IX_LINK_STATISTIC_CONTACTID",columnList = "contactId")
})
public class LinkStatisticEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Type(type = "uuid-char")
    UUID linkId;

    @Type(type = "uuid-char")
    UUID documentId;

    @Column(nullable = false)
    private long visit = 0;

    @Column(nullable = false)
    private long duration = 0;

    @Column(nullable = false)
    private int lastPage = 0;

    @Column(nullable = false)
    private int totalPage = 0;

    @Column(columnDefinition = "FLOAT(53)")
    Double longitude;

    @Column(columnDefinition = "FLOAT(53)")
    Double latitude;

    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    String deviceAgent;

    Long contactId;

    @Column(columnDefinition = "nvarchar(250)")
    String location;

    @Column(nullable = false, columnDefinition = "varchar(40)")
    String deviceId;

    @Column(columnDefinition="nvarchar(50)")
    String deviceName;

    @Column(columnDefinition = "varchar(36)")
    String NDAToken;

    @Column(columnDefinition = "varchar(45)")
    String ip;

    @Column(columnDefinition = "varchar(36)")
    String downloadFileToken;

    Instant authorizedAt;

    OffsetDateTime firstViewedAt =  OffsetDateTime.now(ZoneOffset.UTC);

    OffsetDateTime viewedAt =  OffsetDateTime.now(ZoneOffset.UTC);

    @Column(nullable = false)
    Boolean isPreview = false;

    @Column(nullable = false)
    Boolean downloaded = false;

    @Column(nullable = false)
    Boolean signedNDA = false;

    Long ndaId;

    @Column(nullable = false)
    Boolean verifiedEmail = false;

    @Column(columnDefinition = "nvarchar(150)")
    String bucketKey;

    Boolean fromAllowViewersLink = false;

    Boolean sentInformationEmail = false;
}
