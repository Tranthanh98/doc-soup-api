package logixtek.docsoup.api.features.nda.commands;

import an.awesome.pipelinr.Command;
import lombok.Builder;
import lombok.Data;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
public class CreateCertificate implements Command<Optional<byte[]>> {
    private String documentId;

    private String documentName;

    private String signedChecksum;

    private String originalChecksum;

    private String signature;

    private Integer pageCount;

    private BufferedImage thumbnail;

    private UUID linkId;

    private Long viewerId;
}
