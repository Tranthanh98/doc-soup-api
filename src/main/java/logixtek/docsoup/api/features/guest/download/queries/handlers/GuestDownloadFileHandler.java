package logixtek.docsoup.api.features.guest.download.queries.handlers;

import an.awesome.pipelinr.Command;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import logixtek.docsoup.api.features.guest.download.queries.GuestDownloadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component("GuestDownloadFileHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GuestDownloadFileHandler implements Command.Handler<GuestDownloadFile, ResponseEntity<Resource>> {
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public ResponseEntity<Resource> handle(GuestDownloadFile query) {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, query.getResourceId()));
        if(s3Object != null) {
            var s3ObjectInputStream =  s3Object.getObjectContent();
            var resource = new InputStreamResource(s3ObjectInputStream);

            HttpHeaders headers = new HttpHeaders();
            var fileName = "download-"+s3Object.getKey()+"."+query.getFileType();
            headers.set("Content-Disposition", String.format("attachment; filename="+ fileName));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(s3Object.getObjectMetadata().getContentLength())
                    .contentType(MediaType.valueOf(s3Object.getObjectMetadata().getContentType()))
                    .body(resource);
        }

        return ResponseEntity.noContent().build();
    }
}
