package logixtek.docsoup.api.features.share.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
@Setter
public class SendEmailDeniedVisitRequestMessage {
    UUID linkId;
}
