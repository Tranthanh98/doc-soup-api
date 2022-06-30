package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;

public interface FileStatisticOnContact {
    Long getFileId();
    String getFileDisplayName();
    Long getTimeSpent();
    Integer getVisits();
    OffsetDateTime getLastActivity();
    Integer getVersion();
}
