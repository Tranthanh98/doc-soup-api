package logixtek.docsoup.api.features.link.statistic.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Data
public class CreateLinkStatistic implements Command<ResultOf<LinkStatisticEntity>> {

    @NotNull
    UUID linkId;

    @NotNull
    String deviceId;

    @NotNull
    String deviceAgent;

    @Nullable
    UUID documentId;

    @Nullable
    Double longitude;

    @Nullable
    Double latitude;

    @Nullable
    String ip;

    @Nullable
    Instant authorizedAt;

    Boolean isPreview = false;

    Boolean verifiedEmail = false;

}
