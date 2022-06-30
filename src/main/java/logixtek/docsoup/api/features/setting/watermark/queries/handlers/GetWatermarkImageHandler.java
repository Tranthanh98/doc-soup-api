package logixtek.docsoup.api.features.setting.watermark.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.setting.watermark.queries.GetWatermarkImage;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.WatermarkRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component("GetWatermarkImageHandler")
@AllArgsConstructor
public class GetWatermarkImageHandler implements Command.Handler<GetWatermarkImage, ResponseEntity<Resource>> {

    private  final WatermarkRepository _repository;

    private  final PermissionService permissionService;

    private static final Logger logger = LoggerFactory.getLogger(GetWatermarkImageHandler.class);

    @Override
    public ResponseEntity<Resource> handle(GetWatermarkImage query) {

        var watermarkOption = _repository.findById(query.getId());

        if(watermarkOption.isPresent())
        {
            var item = watermarkOption.get();
            if(permissionService.get(item,query).isDenied())
            {
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            try {
                var length = (int) item.getImage().length();

                var byteData = item.getImage().getBytes(1, length);

                InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteData));

                var extension = item.getImageType() =="image/png" ?".png":".jpeg";

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Disposition", String.format("attachment; filename=watermark-"+ query.getId().toString()+ extension));
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(byteData.length)
                        .contentType(MediaType.valueOf("application/octet-stream"))
                        .body(resource);

            }catch (Exception ex) {
                logger.error(ex.getMessage(), ex);

                return ResponseEntity.internalServerError().build();
            }
        }

        return  ResponseEntity.notFound().build();

    }
}
