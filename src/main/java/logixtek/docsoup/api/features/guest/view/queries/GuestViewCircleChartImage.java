package logixtek.docsoup.api.features.guest.view.queries;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class GuestViewCircleChartImage  implements Command<ResponseEntity<byte[]>> {
    Float percent;
}
