package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.queries.DownloadAllFileOfDataRoomContent;
import logixtek.docsoup.api.infrastructure.models.FileInfo;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component("DownloadAllFileOfDataRoomContentHandler")
@AllArgsConstructor
public class DownloadAllFileOfDataRoomContentHandler implements Command.Handler<DownloadAllFileOfDataRoomContent, ResponseEntity<Resource>> {

    private final LinkRepository linkRepository;
    private final DataRoomRepository dataRoomRepository;
    private final FileContentRepository fileContentRepository;
    private final LinkStatisticRepository linkStatisticRepository;
    private static final Logger logger = LoggerFactory.getLogger(DownloadAllFileOfDataRoomContentHandler.class);

    @SneakyThrows
    @Override
    public ResponseEntity<Resource> handle(DownloadAllFileOfDataRoomContent query) {

        if (query.getDeviceId() == null
                || query.getDeviceId().isBlank()
                || query.getViewerId() == null) {
            return ResponseEntity.badRequest().build();
        }

        var getViewer = linkStatisticRepository.findById(query.getViewerId());

        if (getViewer.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var viewer = getViewer.get();

        if( !viewer.getLinkId().equals(query.getLinkId())
        ){
            return ResponseEntity.notFound().build();
        }

        if (!viewer.getDeviceId().equals(query.getDeviceId())
                || viewer.getAuthorizedAt() == null
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var linkOption = linkRepository
                .findLinkWithLinkAccountById(viewer.getLinkId());

        if(Boolean.TRUE.equals(linkOption.isEmpty())){
            return ResponseEntity.notFound().build();
        }

        var link = linkOption.get();

        if(Boolean.TRUE.equals(!link.getDownload())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var filesOption = dataRoomRepository.getAllFileOfDataRoomContent(link.getRefId());

        if(filesOption.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var zipFileContent = zip(filesOption.get());

        if (zipFileContent != null) {

            linkStatisticRepository.saveAndFlush(viewer);

            var byteData = zipFileContent.toByteArray();

            var resource = new InputStreamResource(new ByteArrayInputStream(byteData));

            var headers = new HttpHeaders();
            headers.set("Content-Disposition",
                    String.format("attachment; filename=Files-of-%s.zip", link.getName()));
            return ResponseEntity.ok().headers(headers).contentLength(byteData.length)
                    .contentType(MediaType.valueOf("application/zip")).body(resource);
        }
        else{
            
            return ResponseEntity.internalServerError().build();
        }
    }

    private ByteArrayOutputStream zip(List<FileInfo> files) {


        try(var bos = new ByteArrayOutputStream();
            var zos = new ZipOutputStream(bos)){
            Blob blob = null;
            for (FileInfo file : files) {

                if(file.getId() == null) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                }
                else{
                    var contentOption = fileContentRepository.findById(file.getId());
                    if (contentOption.isPresent()) {
                        blob = contentOption.get().getContent();

                        var contentLength = (int) blob.length();

                        var byteData = blob.getBytes(1, contentLength);

                        var fileName = file.getName() + file.getExtension();
                        zos.putNextEntry(new ZipEntry(fileName));

                        zos.write(byteData);
                    }
                }
                zos.closeEntry();

            }

            if(blob != null){
                blob.free();
            }

            return bos;
        }
        catch (IOException | SQLException ioe) {
            logger.error(ioe.getMessage(), ioe);
            return null;
        }

    }

}
