package logixtek.docsoup.api.features.nda.domainEventHandlers;

import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import logixtek.docsoup.api.features.nda.commands.CreateCertificate;
import logixtek.docsoup.api.features.nda.commands.CreateSignedNda;
import logixtek.docsoup.api.features.share.domainevents.CreateCertificateAndSignedDomainEvent;
import logixtek.docsoup.api.infrastructure.constants.DownloadConstant;
import logixtek.docsoup.api.infrastructure.helper.ContentHelper;
import logixtek.docsoup.api.infrastructure.helper.Helper;
import logixtek.docsoup.api.infrastructure.repositories.*;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component("CreateCertificateAndSignedDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreateCertificateAndSignedDomainEventHandler implements Notification.Handler<CreateCertificateAndSignedDomainEvent> {
    private final LinkRepository linkRepository;
    private final ContactRepository contactRepository;
    private final LinkStatisticRepository linkStatisticRepository;
    private final FileRepository fileRepository;
    private final FileContentRepository fileContentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreateCertificateAndSignedDomainEventHandler.class);

    private final Pipeline pipeline;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public void handle(CreateCertificateAndSignedDomainEvent domainEvent) {

        var linkOption = linkRepository.findById(domainEvent.getLinkId());
        var viewerOption = linkStatisticRepository.findById(domainEvent.getViewerId());
        var contactOption = contactRepository.findById(domainEvent.getContactId());

        if(contactOption.isPresent() && linkOption.isPresent() && viewerOption.isPresent()){
            var contact = contactOption.get();

            var link = linkOption.get();
            var fileOption = fileRepository.findById(link.getNdaId());
            var ndaFileOption = fileContentRepository.findById(link.getNdaId());

            if(fileOption.isPresent() && ndaFileOption.isPresent()){
                var file = fileOption.get();

                try {
                    var documentCertificateNdaId = UUID.randomUUID().toString();

                    var content = ContentHelper.convertBlobToByte(ndaFileOption.get().getContent());

                    var commandCreateSignedNda = CreateSignedNda.builder()
                            .date(LocalDate.now(ZoneOffset.UTC))
                            .email(contact.getEmail())
                            .fullName(contact.getName())
                            .originNda(content)
                            .documentCertificateId(documentCertificateNdaId)
                            .build();

                    var signedNdaOption = commandCreateSignedNda.execute(pipeline);

                    if(signedNdaOption.isPresent()){
                        var signedNdaByteArray = signedNdaOption.get();

                        var signedCheckSum = Helper.checksumFile(signedNdaByteArray);

                        PDDocument doc = Loader.loadPDF(content);

                        if(doc.isEncrypted()){
                            doc.setAllSecurityToBeRemoved(true);
                        }

                        PDFRenderer pdfRenderer = new PDFRenderer(doc);

                        BufferedImage thumbnail = pdfRenderer.renderImage(0,  (float)0.2);

                        var originCheckSum = Helper.checksumFile(content);

                        var commandCreateCertificate = CreateCertificate.builder()
                                .documentId(documentCertificateNdaId)
                                .documentName(file.getDisplayName())
                                .signedChecksum(signedCheckSum)
                                .originalChecksum(originCheckSum)
                                .signature(contact.getName())
                                .thumbnail(thumbnail)
                                .pageCount(doc.getNumberOfPages())
                                .linkId(domainEvent.getLinkId())
                                .viewerId(domainEvent.getViewerId())
                                .build();

                        var certificateOption = commandCreateCertificate.execute(pipeline);

                        if(certificateOption.isPresent()){
                            var certificateStreams = new ByteArrayInputStream(certificateOption.get());

                            var signedStreams = new ByteArrayInputStream(signedNdaByteArray);

                            var zipFile = zip(certificateStreams, signedStreams);

                            if(zipFile!=null)
                            {
                                var inputStream = new ByteArrayInputStream(zipFile.toByteArray());

                                var bucketKey = uploadToS3(documentCertificateNdaId, inputStream, domainEvent.getLinkId().toString() + domainEvent.getViewerId());

                                var viewer = viewerOption.get();

                                viewer.setBucketKey(bucketKey);

                                linkStatisticRepository.saveAndFlush(viewer);
                            }
                            certificateStreams.close();
                            signedStreams.close();

                        }
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

        }
    }

    private ByteArrayOutputStream zip(InputStream certificate, InputStream signed) {
        try(var bos = new ByteArrayOutputStream();
            var zos = new ZipOutputStream(bos)){

            var certificateName = "certificate.pdf";

            zos.putNextEntry(new ZipEntry(certificateName));

            zos.write(certificate.readAllBytes());

            var signedName = "signed.pdf";

            zos.putNextEntry(new ZipEntry(signedName));

            zos.write(signed.readAllBytes());

            zos.flush();

            zos.closeEntry();

            return bos;
        }
        catch (IOException e){
            logger.error(e.getMessage());
        }

        return null;

    }

    private String uploadToS3(String fileName, InputStream inputStream, String path) {
        var bucketKey = DownloadConstant.NDAFolder + "_" + path + "/" + fileName;

        var metaData = new ObjectMetadata();
        metaData.setContentType("application/zip");

        var putObjectRequest = new PutObjectRequest(bucketName, bucketKey, inputStream, metaData);
        amazonS3.putObject(putObjectRequest);

        return bucketKey;

    }

}
