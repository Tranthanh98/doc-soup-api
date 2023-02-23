package logixtek.docsoup.api.infrastructure.thirdparty;

import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public interface DocumentViewService {
    void downloadFile(String id, OutputStream outputStream) throws IOException;

    String getThumbnailLink(String fileId) throws IOException;

    Optional<DocumentInfo> updateFile(String fileId, MultipartFile file);


}
