package logixtek.docsoup.api.features.link.commands.handlers;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import logixtek.docsoup.api.features.link.commands.AddAllowViewer;
import logixtek.docsoup.api.features.link.models.SecureValue;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("AddAllowViewerHandler")
@AllArgsConstructor
public class AddAllowViewerHandler implements Command.Handler<AddAllowViewer, ResponseMessageOf<String>> {
    private final LinkRepository linkRepository;
    private final PermissionService permissionService;
    private final EncryptService encryptService;

    private final Logger logger = LoggerFactory.getLogger(AddAllowViewerHandler.class);

    @Override
    public ResponseMessageOf<String> handle(AddAllowViewer command) {
        var linkOption = linkRepository.findById(command.getLinkId());

        if(linkOption.isEmpty()){
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        var link = linkOption.get();

        if(!permissionService.getOfLink(link, command).canWrite()){
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        if(command.getToken() != null && Strings.isNotBlank(command.getToken())) {
            var jsonString = encryptService.decrypt(command.getToken());
            if(jsonString != null && Strings.isNotBlank(jsonString)) {
                var linkId = Utils.getJsonValue(jsonString, "linkId", String.class);
                var email = Utils.getJsonValue(jsonString, "email", String.class);
                if(!command.getLinkId().toString().equals(linkId)) {
                    return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
                }

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    var secure = objectMapper.readValue(link.getSecure(), SecureValue.class);
                    if(secure.getEmailViewers().stream().anyMatch(x -> x.equals(email))) {
                        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
                    }

                    secure.getEmailViewers().add(email);

                    link.setSecure(objectMapper.writeValueAsString(secure));
                    linkRepository.saveAndFlush(link);

                    return ResponseMessageOf.of(HttpStatus.ACCEPTED, email);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }

        return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
    }
}
