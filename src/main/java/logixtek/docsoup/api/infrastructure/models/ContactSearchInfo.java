package logixtek.docsoup.api.infrastructure.models;

import java.time.Instant;

public interface ContactSearchInfo {
    Long getId();
    String getName();
    String getLinkAccountNames();
    Instant getLastActivity();
}
