package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;

public interface SimplifiedViewer {
    Long getViewerId();
    String getEmail();
    OffsetDateTime getViewedAt();
    String getFileName();
    Long getDuration();
    Float getViewedRate();
    String getDevice();
    String getContactName();
}
