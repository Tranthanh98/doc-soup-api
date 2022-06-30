package logixtek.docsoup.api.features.share.document.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper=true)
public class CreateDocument extends BaseIdentityCommand<ResultOf<UUID>> {
    @NotNull
    private MultipartFile multipartFile;

    private Boolean download;

    private Boolean save;

    private String lifeSpan;

    private Boolean print;

    private String docName;

    private OffsetDateTime expiredAt;

}
