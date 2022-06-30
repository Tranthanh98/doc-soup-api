package logixtek.docsoup.api.features.setting.watermark.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.WatermarkEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

public class GetWatermark extends BaseIdentityCommand<ResponseEntity<WatermarkEntity>> {

    public static   GetWatermark of(Long id)
    {
        var instance = new GetWatermark();
        instance.setId(id);
        return  instance;
    }

    @Getter
    @Setter
    Long id;
}

