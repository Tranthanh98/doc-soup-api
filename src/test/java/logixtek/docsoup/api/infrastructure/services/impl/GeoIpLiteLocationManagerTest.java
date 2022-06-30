package logixtek.docsoup.api.infrastructure.services.impl;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

class GeoIpLiteLocationManagerTest {

    @Test
    public void givenIP_whenFetchingCity_thenReturnsCityData()
            throws IOException, GeoIp2Exception {
        String ip = "14.169.30.115";
        Resource resource = new ClassPathResource("localDB/GeoLite2-City.mmdb");

        File database = resource.getFile();

        DatabaseReader dbReader = new DatabaseReader.Builder(database)
                .build();

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);

        String countryName = response.getCountry().getName();
        String cityName = response.getCity().getName();
        //String postal = response.getPostal().getCode();
        //String state = response.getLeastSpecificSubdivision().getName();

        Double longitude = response.getLocation().getLongitude();
        Double latitude =  response.getLocation().getLatitude();

        System.out.println(countryName);
        System.out.println(cityName);

        System.out.println(longitude);
        System.out.println(latitude);
    }

}