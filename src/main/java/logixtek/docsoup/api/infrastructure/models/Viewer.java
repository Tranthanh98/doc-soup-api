package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;


public interface Viewer {
    Long getContactId();
    Long getViewerId();
    String getEmail();
    String getLinkCreatorEmail();
    OffsetDateTime getViewedAt();
    String getDevice();
    String getLocationName();
    String getSender();
    Long getDuration();
    Float getViewedRate();
    Boolean getIsPreview();
    Boolean getSignedNDA();
    Long getNdaId();
    Boolean getVerifiedEmail();
    Boolean getDownloaded();
    String getLinkCreator();
}
