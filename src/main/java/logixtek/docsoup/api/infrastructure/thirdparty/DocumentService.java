package logixtek.docsoup.api.infrastructure.thirdparty;

import logixtek.docsoup.api.infrastructure.models.Result;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentInfo;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentOption;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface DocumentService {

    Optional<DocumentInfo> create(MultipartFile multipartFile) throws IOException;

    Optional<DocumentInfo> create(MultipartFile multipartFile, DocumentOption option) throws IOException;

    Optional<DocumentInfo> create(byte[] content, String fileName, DocumentOption option) throws IOException;

    Result Delete(String secureId) throws IOException;

    Result ExtendLife(String streamDocId, String expiredAt) throws IOException;

    ResultOf<String> Register(String externalId) throws IOException;

    ResultOf<byte[]> getThumbnail(String docId,Integer pageNumber) throws IOException;

}
