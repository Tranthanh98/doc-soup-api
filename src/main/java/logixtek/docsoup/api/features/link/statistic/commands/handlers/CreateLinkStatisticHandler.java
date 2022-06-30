package logixtek.docsoup.api.features.link.statistic.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.statistic.commands.CreateLinkStatistic;
import logixtek.docsoup.api.features.link.statistic.mappers.LinkStatisticEntityMapper;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.services.LocationManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

@Component("CreateLinkStatisticHandler")
@RequiredArgsConstructor
public class CreateLinkStatisticHandler implements Command.Handler<CreateLinkStatistic, ResultOf<LinkStatisticEntity>>{

    private  final LinkStatisticRepository repository;
    private  final LocationManager locationManager;
    private static final Logger logger = LoggerFactory.getLogger(CreateLinkStatisticHandler.class);

    @Override
    public ResultOf<LinkStatisticEntity> handle(CreateLinkStatistic command) {

        try {
            var option = repository.findFirstByLinkIdAndDeviceId(command.getLinkId(), command.getDeviceId());

            if (option.isPresent()) {
                return ResultOf.of(option.get());
            }

            if (command.getDeviceId().isEmpty() || command.getDeviceId().isBlank()) {
                return ResultOf.of(false, "The deviceId is mandatory");
            }

            if (command.getDeviceAgent().isEmpty() || command.getDeviceAgent().isBlank()) {
                return ResultOf.of(false, "The user agent is mandatory");
            }

            var entity = LinkStatisticEntityMapper.INSTANCE.toEntity(command);

            entity.setDeviceName(getDeviceName(command.getDeviceAgent()));

            if (!command.getIp().isBlank() && !command.getIp().isEmpty()) {
                var location = locationManager.getLocation(command.getIp());

                if (location != null) {

                    var locationName = new StringBuilder();
                    if(location.getCity()!=null && !location.getCity().isEmpty()) {
                    	locationName.append(location.getCity());
                    }
                    
                    if(location.getCountry()!=null && !location.getCountry().isEmpty()) {
                    	locationName.append(", "+location.getCountry());
                    }
                    
					entity.setLocation(locationName.toString());
				}
                
                if(entity.getLongitude() == null || entity.getLatitude() == null || entity.getLatitude() < 1 || entity.getLongitude()  < 1)
                {
                    entity.setLongitude(location.getLongitude());
                    entity.setLatitude(location.getLatitude());
                }
            }

            entity = repository.saveAndFlush(entity);

            return ResultOf.of(entity);

        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
            return  ResultOf.of(false,"Sorry. Unexpected exception.");
        }

    }

    private String getDeviceName(String userAgent)
    {
        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);
        if(client!=null)
        {
            return getOSTypeName(userAgent) + " - " + client.os.family + " - " + client.userAgent.family;
        }

        return "";
    }

    private String getOSTypeName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Others";
        } else if (userAgent.toLowerCase().contains("windows")) {
            return "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            return "Mac";
        } else if (userAgent.toLowerCase().contains("x11")) {
            return"Linux";
        } else if (userAgent.toLowerCase().contains("android")) {
            return "Android";
        } else if (userAgent.toLowerCase().contains("iphone")) {
            return"Iphone";
        } else {
            return "Others";
        }
    }
}
