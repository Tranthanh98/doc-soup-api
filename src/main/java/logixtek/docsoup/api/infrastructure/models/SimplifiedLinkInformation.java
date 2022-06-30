package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface SimplifiedLinkInformation {

    UUID getId();
    Long getLinkAccountsId();
    String getName();
    String getOwner();
    OffsetDateTime getCreateDate();
}
