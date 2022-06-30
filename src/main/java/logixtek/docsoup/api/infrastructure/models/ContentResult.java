package logixtek.docsoup.api.infrastructure.models;

import java.time.Instant;

public interface ContentResult {
    long getId();
    String getDisplayName();
    Boolean getIsTeam();
    String getLocationPath();
    Instant getUpdateDate();
    String getOwner();
    int getTotalRows();
    Boolean getIsFile();
}
