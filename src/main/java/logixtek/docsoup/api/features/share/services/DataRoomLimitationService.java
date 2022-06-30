package logixtek.docsoup.api.features.share.services;

import java.util.UUID;

public interface DataRoomLimitationService {
    Boolean isAllow(UUID companyId, Long dataRoomId);

    Boolean isAllow(UUID companyId);

    Boolean isDuplicate(UUID companyId, Long duplicatedDataRoomId);
}
