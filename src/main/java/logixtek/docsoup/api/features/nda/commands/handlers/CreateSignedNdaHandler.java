package logixtek.docsoup.api.features.nda.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.nda.builders.SignedNda;
import logixtek.docsoup.api.features.nda.commands.CreateSignedNda;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("CreateSignedNdaHandler")
@AllArgsConstructor
public class CreateSignedNdaHandler implements Command.Handler<CreateSignedNda, Optional<byte[]>> {

    @Override
    public Optional<byte[]> handle(CreateSignedNda command) {

        var out = new SignedNda().fullName(command.getFullName())
                .date(command.getDate().toString())
                .email(command.getEmail())
                .originNda(command.getOriginNda())
                .documentCertificateId(command.getDocumentCertificateId())
                .build();

        if(out != null){
            var byteArray = out.toByteArray();

            return Optional.of(byteArray);
        }

        return Optional.empty();

    }
}
