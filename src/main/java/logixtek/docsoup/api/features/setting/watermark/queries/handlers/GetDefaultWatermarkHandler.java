package logixtek.docsoup.api.features.setting.watermark.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.setting.watermark.queries.GetDefaultWatermark;
import logixtek.docsoup.api.infrastructure.entities.WatermarkEntity;
import logixtek.docsoup.api.infrastructure.repositories.WatermarkRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetDefaultWatermarkHandler")
@AllArgsConstructor
public class GetDefaultWatermarkHandler implements Command.Handler<GetDefaultWatermark,
        ResponseEntity<WatermarkEntity>> {

    private  final WatermarkRepository _repository;
    @Override
    public ResponseEntity<WatermarkEntity> handle(GetDefaultWatermark query) {
        var watermarkOption = _repository.findFirstByAccountIdAndCompanyIdAndIsDefaultIsTrue(query.getAccountId(),query.getCompanyId());

        if(watermarkOption.isPresent())
        {
            return  ResponseEntity.ok(watermarkOption.get());
        }

        return  ResponseEntity.noContent().build();

    }
}
