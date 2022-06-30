package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;


public interface LinkStatistic {
    Long getId();
    UUID getLinkId();
    String getLinkName();
    OffsetDateTime getCreatedDate();
    OffsetDateTime getViewedAt();
    Integer getActivity();
    Long getDuration();
    Boolean getDisabled();
    Long getLinkAccountsId();
    Long getRefId();
    String getLinkCreatorEmail();
    String getLinkCreatorName();
}
