package logixtek.docsoup.api.features.setting.watermark.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.setting.watermark.commands.CreateWatermark;
import logixtek.docsoup.api.features.setting.watermark.commands.UpdateWatermark;
import logixtek.docsoup.api.features.share.commands.UploadFileCommand;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.WatermarkRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;


@Component("UpdateWatermarkHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateWatermarkHandler implements Command.Handler<UpdateWatermark, ResponseMessageOf<String>> {

    @Value("${docsoup.setting.watermark.image.size}")
    private Long maxImageSize;

    private final WatermarkRepository _repository;

    private  final PermissionService permissionService;

    private static final Logger logger = LoggerFactory.getLogger(UpdateWatermarkHandler.class);

    @Override
    public ResponseMessageOf<String> handle(UpdateWatermark command) {

        var itemOption = _repository.findById(command.getId());

        if(itemOption.isEmpty())
        {
            return  ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        var entity = itemOption.get();

        if(permissionService.get(entity,command).isDenied())
        {
            return  ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        entity.setText(command.getText());


        if(command.getImage() != null)
        {
            var imageType = command.getImage().getContentType();

            if(imageType == null){
                return new ResponseMessageOf<String>(HttpStatus.BAD_REQUEST, "Invalid image type",
                        Map.of(CreateWatermark.Fields.image, "Invalid image type"));
            }

            var isAllowedImageType = imageType.equalsIgnoreCase("image/png")
                    || imageType.equalsIgnoreCase("image/jpeg");

            if (!isAllowedImageType) {
                return new ResponseMessageOf<String>(HttpStatus.BAD_REQUEST, "Invalid image type",
                        Map.of(UploadFileCommand.Fields.multipartFile, "Invalid image type"));
            }
            try {
                var size = command.getImage().getSize()/1204;

                if(size > maxImageSize)
                {
                    return new ResponseMessageOf<String>(HttpStatus.BAD_REQUEST, "The image is too large",
                            Map.of(CreateWatermark.Fields.image, "The image is too large"));
                }
                entity.setImage(convertFileToBlob(command.getImage()));
                entity.setImageType(imageType);
            }
            catch (Exception ex)
            {
                logger.error(ex.getMessage(), ex);

                return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        _repository.saveAndFlush(entity);

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }

    private Blob convertFileToBlob(MultipartFile file) throws IOException, SQLException {
        var fileBytes = file.getBytes();

        try {
            return new javax.sql.rowset.serial.SerialBlob(fileBytes);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
