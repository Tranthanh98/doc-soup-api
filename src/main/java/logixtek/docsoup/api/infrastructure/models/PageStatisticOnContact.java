package logixtek.docsoup.api.infrastructure.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PageStatisticOnContact extends PageStatistic {
   UUID getLinkId();
   String getLinkName();
   OffsetDateTime getViewedAt();
   Long getViewerId();
}
