package logixtek.docsoup.api.features.content.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class UpgradeFileVersion extends BaseIdentityCommand<ResponseMessageOf<Long>> {
    @Min(1)
    @NotNull
    Long directoryId ;

    @Min(1)
    Long fileId;

    @NotNull(message = "file is mandatory")
    private MultipartFile multipartFile;
}
