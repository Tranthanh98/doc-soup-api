package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface UserCompanyWithPlanTier {
    UUID getCompanyId();
    String getName();
    OffsetDateTime getJoinDate();
    String getPlanTier();
    Integer getMemberType();
    String getRole();
    int getStatus();
}
