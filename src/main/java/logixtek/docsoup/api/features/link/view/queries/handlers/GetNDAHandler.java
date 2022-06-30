package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.view.commands.CreateVisitorHistory;
import logixtek.docsoup.api.features.link.view.queries.GetNDA;
import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.enums.ViewerAction;
import logixtek.docsoup.api.infrastructure.helper.ContentHelper;
import logixtek.docsoup.api.infrastructure.repositories.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component("GetNDAHandler")
@AllArgsConstructor
public class GetNDAHandler implements Command.Handler<GetNDA, ResponseEntity<Resource>> {

    private final LinkStatisticRepository repository;
    private final LinkRepository linkRepository;
    private final FileRepository fileRepository;
    private final FileContentRepository contentRepository;
    private final ContactRepository contactRepository;

    private final Pipeline pipeline;

    private static final Logger logger = LoggerFactory.getLogger(GetNDAHandler.class);

    @Override
    public ResponseEntity<Resource> handle(GetNDA query) {

        try {
            if (query.getDeviceId().isEmpty() || query.getDeviceId().isBlank() || query.getViewerId() == null) {

                return ResponseEntity.badRequest().build();
            }

            var getViewer = repository.findById(query.getViewerId());

            if (!getViewer.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            var viewer = getViewer.get();

            if (!viewer.getDeviceId().equals(query.getDeviceId())
                    || !viewer.getLinkId().toString().equals(query.getLinkId().toString())) {
                return ResponseEntity.notFound().build();
            }

            if (viewer.getNDAToken() == null || !viewer.getNDAToken().equals(query.getToken())) {
                return ResponseEntity.notFound().build();
            }

            var linkOption = linkRepository.findById(viewer.getLinkId());

            if (!linkOption.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            var link = linkOption.get();

            var ndaFileOption = fileRepository.findByIdAndCompanyIdAndNdaIsTrue(
                    link.getNdaId(),link.getCompanyId());

            if (!ndaFileOption.isPresent()) {
                return ResponseEntity.noContent().build();
            }

            var ndaFile = ndaFileOption.get();

            /*Temporary solution*/
            var zipFileName = zip(ndaFile);

            if (zipFileName == null) {
                return ResponseEntity.unprocessableEntity().build();
            }

            var  zipFile = new File(zipFileName);

            var reader = new FileInputStream(zipFile);

            var zipContent = reader.readAllBytes();

            reader.close();

            if(zipFile.delete()){
                repository.saveAndFlush(viewer);

                var contactOption = contactRepository.findById(viewer.getContactId());

                if(contactOption.isPresent()){
                    createHistoryVisitor(query, viewer, contactOption.get());
                }

                InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(zipContent));

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Disposition",
                        String.format("attachment; filename=nda-"+ Instant.now().toString()+".zip"));
                return ResponseEntity.ok().headers(headers).contentLength(zipContent.length)
                        .contentType(MediaType.valueOf("application/zip")).body(resource);
            }

            return  ResponseEntity.badRequest().build();

        } catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            return  ResponseEntity.internalServerError().build();
        }

    }

    public String zip(FileEntity entity) {

        String zipFile = UUID.randomUUID() + ".zip";

        try {

            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(zipFile);

            ZipOutputStream zos = new ZipOutputStream(fos);

            var contentOption = contentRepository.findById(entity.getId());
            if (contentOption.isPresent()) {

                var fis = new ByteArrayInputStream(
                        ContentHelper.convertBlobToByte(contentOption.get().getContent()));

                zos.putNextEntry(new ZipEntry(entity.getDisplayName() + entity.getExtension()));

                int length;

                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();

                fis.close();
            }

            zos.close();
            fos.close();

            return zipFile;

        } catch (IOException | SQLException ioe) {
            logger.error(ioe.getMessage(), ioe);
            return null;
        }

    }

    private Long createHistoryVisitor(GetNDA query, LinkStatisticEntity viewer, ContactEntity contact){
        var deviceName = viewer.getDeviceName().split("-");
        var browserName = deviceName[2];

        var command = CreateVisitorHistory.builder()
                .actionType(ViewerAction.DOWNLOAD_DOCUMENT)
                .email(contact.getEmail())
                .ipAddress(query.getIp())
                .linkId(query.getLinkId())
                .location(viewer.getLocation())
                .name(contact.getName())
                .viewerId(viewer.getId())
                .userAgent(viewer.getDeviceAgent())
                .browserName(browserName)
                .build();

        var result = command.execute(pipeline);
        return result.getBody();
    }
}
