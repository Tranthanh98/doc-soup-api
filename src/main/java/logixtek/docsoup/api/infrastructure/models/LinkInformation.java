package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface LinkInformation {
    UUID getId();

    Long getRefId();

    UUID getDocumentId();

    long getVisit();

    String getName();

    String getSecure();

    Boolean getDownload();

    Long getWatermarkId();

    OffsetDateTime getExpiredAt();

    UUID getParent();

    Integer getStatus();

    UUID getCompanyId();

    Long getNdaId();

    Long getLinkAccountsId();

    OffsetDateTime getCreatedDate();

    String getCreatedBy();

    OffsetDateTime getModifiedDate();

    String getCreatedByName();
}
