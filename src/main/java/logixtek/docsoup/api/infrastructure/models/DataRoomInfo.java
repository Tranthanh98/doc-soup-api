package logixtek.docsoup.api.infrastructure.models;

import java.time.Instant;
import java.util.UUID;

public interface DataRoomInfo {

    long getId();

    String getName();

    Boolean getIsActive();

    String getAccountId();

    UUID getCompanyId();

    Integer getViewType();

    Instant getCreatedDate();

    Instant getModifiedDate();

    String getOwner();

    String getSharedWithAccount();

    Boolean getDisabledAllLink();
}
