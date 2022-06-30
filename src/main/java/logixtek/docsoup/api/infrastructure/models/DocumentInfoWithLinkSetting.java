package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DocumentInfoWithLinkSetting {
    UUID getId();
    Boolean getDownload();
    OffsetDateTime getExpiredAt();
    OffsetDateTime getLifeSpan();
}
