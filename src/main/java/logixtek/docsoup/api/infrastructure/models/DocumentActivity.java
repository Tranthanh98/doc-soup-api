package logixtek.docsoup.api.infrastructure.models;

import java.time.Instant;

public interface DocumentActivity {
    long getId();
    String getDisplayName();
    Instant getRecentActivityDate();
    long getSize();
    Float getViewedRate();
    Long getViews();
}
