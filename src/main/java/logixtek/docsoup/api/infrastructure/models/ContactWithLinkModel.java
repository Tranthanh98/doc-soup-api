package logixtek.docsoup.api.infrastructure.models;

import java.time.Instant;

public interface ContactWithLinkModel {
    Long getContactId();
    String getContactName();
    Integer getVisits();
    Instant getLastActivity();
    Boolean getArchived();
    String getAccountId();
    Boolean getSignedNDA();
    String getLinkNames();
    String getLinkCreators();
}
