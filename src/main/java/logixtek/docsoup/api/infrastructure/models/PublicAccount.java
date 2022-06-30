package logixtek.docsoup.api.infrastructure.models;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface PublicAccount {
    UUID getId();
    String getFullName();
    String getEmail();
    Instant getLastActive();
    UUID getActiveCompanyId();
    String getPhone();
    Instant getRegisterDate();
    OffsetDateTime getCheckInDate();
    Long getUsedSpace();
    Boolean getEnable();
    Integer getTotalRows();
}
