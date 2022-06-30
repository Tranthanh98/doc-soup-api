package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;

public interface SimplifiedDataRoomInfo {
    long getId();
    String getName();
    OffsetDateTime getCreatedDate();
    String getOwner();
    Integer getLinks();
}
