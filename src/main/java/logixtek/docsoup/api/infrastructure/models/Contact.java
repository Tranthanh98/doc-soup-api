package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;

public interface Contact {
    Long getContactId();
    String getContactName();
    Integer getVisits();
    OffsetDateTime getLastActivity();
    Boolean getArchived();
    String getAccountId();
}
