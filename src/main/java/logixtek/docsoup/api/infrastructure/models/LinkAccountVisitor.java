package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;

public interface LinkAccountVisitor {
    String getEmail();

    String getLocation();

    Long getDuration();

    OffsetDateTime getViewedAt();

    Long getVisits();

    Long getNdaId();

    Boolean getSignedNDA();

    String getDevice();

    Long getContactId();
}
