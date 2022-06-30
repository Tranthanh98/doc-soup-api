package logixtek.docsoup.api.features.setting.watermark.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.WatermarkEntity;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class GetDefaultWatermark extends BaseIdentityCommand<ResponseEntity<WatermarkEntity>> {

}

