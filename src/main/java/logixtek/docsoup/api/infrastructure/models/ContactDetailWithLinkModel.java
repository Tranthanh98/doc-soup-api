package logixtek.docsoup.api.infrastructure.models;

import java.util.UUID;

public interface ContactDetailWithLinkModel {
    UUID getId();
    String getLinkName();
    Boolean getSignedNDA();
    Long getViewerId();
}
