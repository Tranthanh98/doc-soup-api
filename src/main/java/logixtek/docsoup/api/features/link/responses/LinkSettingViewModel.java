package logixtek.docsoup.api.features.link.responses;

import logixtek.docsoup.api.features.link.models.SecureValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class LinkSettingViewModel {
    UUID linkId;

    Boolean download;

    Boolean disabled;

    SecureValue secure;

    String name;

    Long watermarkId;

    OffsetDateTime expiredAt;

    Long linkAccountsId;

    Long ndaId;
}