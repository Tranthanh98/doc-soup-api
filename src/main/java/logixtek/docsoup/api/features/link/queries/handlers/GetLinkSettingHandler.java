package logixtek.docsoup.api.features.link.queries.handlers;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.link.models.SecureValue;
import logixtek.docsoup.api.features.link.queries.GetLinkSetting;
import logixtek.docsoup.api.features.link.responses.LinkSettingViewModel;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.constants.LinkConstant;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("GetLinkSettingHandler")
@AllArgsConstructor
public class GetLinkSettingHandler implements Command.Handler<GetLinkSetting, ResponseMessageOf<LinkSettingViewModel>> {
    private final LinkRepository linkEntityRepository;
    private final PermissionService permissionService;
    private final LinkAccountsRepository linkAccountsRepository;

    private static final Logger logger = LoggerFactory.getLogger(GetLinkSettingHandler.class);

    @Override
    public ResponseMessageOf<LinkSettingViewModel> handle(GetLinkSetting query) {
        try {
            var linkOption = linkEntityRepository.findById(query.getLinkId());


            if (linkOption.isPresent()) {
                var link = linkOption.get();
                if(!permissionService.getOfLink(link, query).canRead())
                {
                    return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
                }

                var isLinkDisabled = link.getStatus() > LinkConstant.ACTIVE_STATUS;
                var result = LinkSettingViewModel.builder()
                        .linkId(link.getId())
                        .download(link.getDownload())
                        .disabled(isLinkDisabled)
                        .watermarkId(link.getWatermarkId())
                        .expiredAt(link.getExpiredAt())
                        .ndaId(link.getNdaId())
                        .build();

                var linkAccountOption = linkAccountsRepository.findById(link.getLinkAccountsId());
                if(linkAccountOption.isPresent()) {
                    result.setName(linkAccountOption.get().getName());
                    result.setLinkAccountsId(linkAccountOption.get().getId());
                }

                if(!Strings.isNullOrEmpty(link.getSecure())) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    var secureValue = objectMapper.readValue(link.getSecure(), SecureValue.class);
                    result.setSecure(secureValue);
                }

                return ResponseMessageOf.of(HttpStatus.OK, result);
            }

            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
