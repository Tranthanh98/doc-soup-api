package logixtek.docsoup.api.infrastructure.services.impl;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import logixtek.docsoup.api.infrastructure.services.LocationManager;
import logixtek.docsoup.api.infrastructure.services.models.LocationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
public class GeoIpLiteLocationManager implements LocationManager {


    @Value("${logixtek.docsoup.api.infrastructure.services.dbLocation}")
    String dbLocation;

    private static final Logger logger = LoggerFactory.getLogger(GeoIpLiteLocationManager.class);

    @Override
    public LocationInfo getLocation(String ip)   {

        try
        {
        ClassLoader classLoader = getClass().getClassLoader();
        
        DatabaseReader dbReader = new DatabaseReader.Builder(classLoader.getResourceAsStream(dbLocation))
                .build();

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);

        var location = response.getLocation();

        return LocationInfo.of(response.getCountry().getName(),response.getCity().getName(),
                location.getLongitude(),location.getLatitude());
        }catch(Exception ex)
        {
            logger.error(ex.getMessage(),ex);
        }
        return null;
    }
}
