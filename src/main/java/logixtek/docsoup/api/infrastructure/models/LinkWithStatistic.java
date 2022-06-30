package logixtek.docsoup.api.infrastructure.models;

import java.util.UUID;

public interface LinkWithStatistic extends LinkStatistic {

    Long getRefId();

    UUID getDocumentId();

    Integer getVersion();

}
