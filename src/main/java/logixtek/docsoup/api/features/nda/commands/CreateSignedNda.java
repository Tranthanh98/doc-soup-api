package logixtek.docsoup.api.features.nda.commands;

import an.awesome.pipelinr.Command;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
@Builder
public class CreateSignedNda implements Command<Optional<byte[]>> {
    private String documentCertificateId;

    private String fullName;

    private LocalDate date;

    private String email;

    private byte[] originNda;
}
