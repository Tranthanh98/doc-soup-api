package logixtek.docsoup.api.features.setting.watermark.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public class GetWatermarkImage extends BaseIdentityCommand<ResponseEntity<Resource>> {

    public static GetWatermarkImage of(Long id)
    {
        var instance = new GetWatermarkImage();
        instance.setId(id);
        return  instance;
    }

    @Getter
    @Setter
    Long id;
}

