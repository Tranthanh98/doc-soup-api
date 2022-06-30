package logixtek.docsoup.api.features.setting.watermark.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
public class CreateWatermark extends BaseIdentityCommand<ResponseMessageOf<Long>> {

    @NotNull(message ="text is a mandatory field")
    @Size(max=4000,message = "The text length mus be smaller than 4000")
    String text;

    @Nullable
    MultipartFile image;

    Boolean isDefault = false;
}
