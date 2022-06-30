package logixtek.docsoup.api.features.setting.watermark.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.setting.watermark.commands.CreateWatermark;
import logixtek.docsoup.api.features.setting.watermark.mappers.WatermarkMapper;
import logixtek.docsoup.api.infrastructure.entities.WatermarkEntity;
import logixtek.docsoup.api.infrastructure.repositories.WatermarkRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

import static logixtek.docsoup.api.infrastructure.helper.ContentHelper.convertFileToBlob;


@Component("CreateWatermarkHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreateWatermarkHandler implements Command.Handler<CreateWatermark, ResponseMessageOf<Long>> {

    @Value("${docsoup.setting.watermark.image.size}")
    private Long maxImageSize;

    private final WatermarkRepository _repository;

    private static final Logger logger = LoggerFactory.getLogger(CreateWatermarkHandler.class);

    @Override
    public ResponseMessageOf<Long> handle(CreateWatermark command) {


        try {

            var watermark = WatermarkMapper.INSTANCE.toEntity(command);

            if(command.getImage() != null) {
                var imageType = command.getImage().getContentType();

                if(imageType == null){
                    return new ResponseMessageOf<Long>(HttpStatus.BAD_REQUEST, "Invalid image type",
                            Map.of(CreateWatermark.Fields.image, "Invalid image type"));
                }
                var isAllowedImageType = imageType.equalsIgnoreCase("image/png")
                        || imageType.equalsIgnoreCase("image/jpeg");

                if (!isAllowedImageType) {
                    return new ResponseMessageOf<Long>(HttpStatus.BAD_REQUEST, "Invalid image type",
                            Map.of(CreateWatermark.Fields.image, "Invalid image type"));
                }

                var size = command.getImage().getSize()/1204;

                if(size > maxImageSize)
                {
                    return new ResponseMessageOf<Long>(HttpStatus.BAD_REQUEST, "The image is too large",
                            Map.of(CreateWatermark.Fields.image, "The image is too large"));
                }
                watermark.setImageType(imageType);

                watermark.setImage(convertFileToBlob(command.getImage()));

            }

            WatermarkEntity currentDefaultItem = null;

            if (watermark.getIsDefault()) {
              var  currentDefaultItemOption = _repository.findFirstByAccountIdAndCompanyIdAndIsDefaultIsTrue(
                      command.getAccountId(),command.getCompanyId());

              if(currentDefaultItemOption.isPresent())
              {
                  currentDefaultItem = currentDefaultItemOption.get();
              }
            }

            watermark = _repository.saveAndFlush(watermark);

            if(currentDefaultItem!=null)
            {
                currentDefaultItem.setIsDefault(false);
                _repository.saveAndFlush(currentDefaultItem);
            }

            return ResponseMessageOf.of(HttpStatus.CREATED,watermark.getId());


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



}
