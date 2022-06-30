package logixtek.docsoup.api.features.setting.watermark.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.setting.watermark.queries.GetWatermark;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.WatermarkEntity;
import logixtek.docsoup.api.infrastructure.repositories.WatermarkRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetWatermarkHandler")
@AllArgsConstructor
public class GetWatermarkHandler implements Command.Handler<GetWatermark,
        ResponseEntity<WatermarkEntity>> {

    private  final WatermarkRepository _repository;
    private final PermissionService permissionService;
    @Override
    public ResponseEntity<WatermarkEntity> handle(GetWatermark query) {
        var watermarkOption = _repository.findById(query.getId());

        if(watermarkOption.isPresent())
        {
            var item = watermarkOption.get();
            if(permissionService.get(item,query).isDenied())
            {
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return  ResponseEntity.ok(item);
        }

        return  ResponseEntity.notFound().build();

    }
}
