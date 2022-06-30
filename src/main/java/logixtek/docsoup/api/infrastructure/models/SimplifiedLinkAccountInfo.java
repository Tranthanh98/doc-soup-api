package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;

public interface SimplifiedLinkAccountInfo {
    Long getId();
    String getName();
    OffsetDateTime getLastActivity();
}
