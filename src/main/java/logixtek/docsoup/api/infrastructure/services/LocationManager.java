package logixtek.docsoup.api.infrastructure.services;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import logixtek.docsoup.api.infrastructure.services.models.LocationInfo;

import java.io.IOException;

public interface LocationManager {
    LocationInfo getLocation(String ip) throws IOException, GeoIp2Exception;
}
