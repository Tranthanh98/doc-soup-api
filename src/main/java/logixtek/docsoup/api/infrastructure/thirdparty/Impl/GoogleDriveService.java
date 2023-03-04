package logixtek.docsoup.api.infrastructure.thirdparty.Impl;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import logixtek.docsoup.api.infrastructure.models.Result;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentViewService;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentInfo;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentOption;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Primary
public class GoogleDriveService implements DocumentService, DocumentViewService {
    private final Drive googleDriveService;

    @Value("${google.drive.folder-id}")
    private String folderId;

    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveService.class);

    @Override
    public Optional<DocumentInfo> create(MultipartFile file) throws IOException {
        try {
            if (null != file) {

                File fileMetadata = new File();
                fileMetadata.setParents(Collections.singletonList(folderId));
                fileMetadata.setName(file.getOriginalFilename());

                File uploadFile = googleDriveService
                        .files()
                        .create(fileMetadata, new InputStreamContent(
                                file.getContentType(),
                                new ByteArrayInputStream(file.getBytes())))
                        .setFields("id").execute();

                var result = new DocumentInfo();
                result.setSecureId(uploadFile.getId());
                result.setFileSize(uploadFile.getSize());
                result.setStreamdocsId(uploadFile.getDriveId());
                result.setDocName(uploadFile.getName());
                result.setGivenName(uploadFile.getOriginalFilename());
                result.setAlink("gg drive system");

                return Optional.of(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<DocumentInfo> create(MultipartFile file, DocumentOption option) throws IOException {
        return this.create(file);
    }

    @Override
    public Optional<DocumentInfo> create(byte[] content, String fileName, DocumentOption option) throws IOException {
        try {
            if (null != content) {

                File fileMetadata = new File();
                fileMetadata.setParents(Collections.singletonList(folderId));
                fileMetadata.setName(option.getDocName());
                File uploadFile = googleDriveService
                        .files()
                        .create(fileMetadata, new InputStreamContent(
                                "application/pdf",
                                new ByteArrayInputStream(content)))
                        .setFields("id").execute();

                var result = new DocumentInfo();
                result.setSecureId(uploadFile.getId());
                result.setFileSize(uploadFile.getSize());
                result.setStreamdocsId(uploadFile.getId());
                result.setDocName(uploadFile.getName());
                result.setGivenName(uploadFile.getOriginalFilename());
                result.setAlink("gg drive system");

                return Optional.of(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Result Delete(String fileId) throws IOException {
        googleDriveService.files().delete(fileId).execute();

        return ResultOf.of(true, "delete success");
    }

    @Override
    public Result ExtendLife(String streamDocId, String expiredAt) throws IOException {
        return null;
    }

    @Override
    public ResultOf<String> Register(String externalId) throws IOException {
        return null;
    }

    @Override
    public ResultOf<byte[]> getThumbnail(String docId, Integer pageNumber) throws IOException {
        var document = googleDriveService.files().get(docId)
                .setFields("thumbnailLink, hasThumbnail")
                .execute();
        ;

        if (!document.getHasThumbnail()) {
            return ResultOf.of(false);
        }

        var thumbnailLink = document.getThumbnailLink();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        var urlBuildr = new StringBuilder();
        urlBuildr.append(thumbnailLink);

        Request request = new Request.Builder()
                .url(urlBuildr.toString())
                .method("GET", null)
                .addHeader("Content-Type", "image/jpeg")
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (response.code() >= 200 && response.code() <= 299) {
                var byteResponse = response.body() != null ? Objects.requireNonNullElse(response.body(), null).bytes()
                        : null;

                if (byteResponse != null) {
                    return ResultOf.of(byteResponse);
                }

                return ResultOf.of(false, "GoogleDriveService.getThumbnail was done without bodyOption.");

            } else {
                logger.error("GoogleDriveService.getThumbnail was failed with http status = " + response.code());

                return ResultOf.of(false, response.message());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return ResultOf.of(false, "GoogleDriveService.getThumbnail was failed with Unknown exception");

    }

    @Override
    public void downloadFile(String id, OutputStream outputStream) throws IOException {
        if (id != null) {
            googleDriveService.files()
                    .get(id).executeMediaAndDownloadTo(outputStream);
        }
    }

    @Override
    public String getThumbnailLink(String fileId) throws IOException {
        if (fileId != null) {
            return googleDriveService.files().get(fileId).execute().getThumbnailLink();
        }

        return null;
    }

    @Override
    public Optional<DocumentInfo> updateFile(String fileId, MultipartFile multipartFile) {
        try {
            // First retrieve the file from the API.
            File file = googleDriveService.files().get(fileId).execute();

            // File's new metadata.
            file.setMimeType(multipartFile.getContentType());

            // File's new content.
            java.io.File fileContent = new java.io.File(multipartFile.getName());
            FileContent mediaContent = new FileContent(multipartFile.getContentType(), fileContent);

            // Send the request to the API.
            File updatedFile = googleDriveService.files().update(fileId, file, mediaContent).execute();

            var result = new DocumentInfo();
            result.setSecureId(updatedFile.getId());
            result.setFileSize(updatedFile.getSize());
            result.setStreamdocsId(updatedFile.getId());
            result.setDocName(updatedFile.getName());
            result.setGivenName(updatedFile.getOriginalFilename());
            result.setAlink("gg drive system");

            return Optional.of(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }
}
