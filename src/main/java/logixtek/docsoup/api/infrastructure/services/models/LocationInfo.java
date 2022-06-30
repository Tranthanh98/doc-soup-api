package logixtek.docsoup.api.infrastructure.services.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class LocationInfo {

    String country;

    String city;

    Double longitude;

    Double latitude;
}
