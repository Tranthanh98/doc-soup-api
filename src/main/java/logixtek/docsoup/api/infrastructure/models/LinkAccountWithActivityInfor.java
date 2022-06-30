package logixtek.docsoup.api.infrastructure.models;

import java.time.Instant;

public interface LinkAccountWithActivityInfor {
    Long getId();
    String getName();
    String getContributors();
    Instant getLastActivity();
    Long getTotalVisit();
    Long getTotalVisitor();
    Long getTotalDuration();
    Boolean getArchived();
}
