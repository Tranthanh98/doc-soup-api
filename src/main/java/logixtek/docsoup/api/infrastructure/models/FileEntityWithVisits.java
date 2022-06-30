package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface FileEntityWithVisits {
    Long getId();
    Long getDirectoryId();
    String getDisplayName();
    Long getSize();
    String getExtension();
    Boolean getNda();
    String getAccountId();
    Long getRecentVisits();
    UUID getDocumentId();
    UUID getCompanyId();
    OffsetDateTime getModifiedDate();
    OffsetDateTime getCreatedDate();
    String getOwnerName();
    Integer getLinks();
    Integer getVersion();
}
