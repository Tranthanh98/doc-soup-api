package logixtek.docsoup.api.features.administrator.plantier.featureflag.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.plantier.featureflag.commands.AdminUpdateFeatureFlag;
import logixtek.docsoup.api.infrastructure.repositories.FeatureFlagRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

@AllArgsConstructor
@Component("AdminUpdateFeatureFlagHandler")
public class AdminUpdateFeatureFlagHandler implements Command.Handler<AdminUpdateFeatureFlag, ResponseMessageOf<String>> {
    private final FeatureFlagRepository featureFlagRepository;


    @Override
    public ResponseMessageOf<String> handle(AdminUpdateFeatureFlag command) {


        var featureFlagUpdate = featureFlagRepository.findById(command.getFeatureFlagId());


        if(featureFlagUpdate.isEmpty()){

            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);

        }


        if(Objects.equals(featureFlagUpdate.get().getLimit(), command.getLimit())){

            return ResponseMessageOf.of(HttpStatus.ACCEPTED);

        }


        var newFeatureFlag = featureFlagUpdate.get();


        newFeatureFlag.setLimit(command.getLimit());


        featureFlagRepository.saveAndFlush(newFeatureFlag);


        return ResponseMessageOf.of(HttpStatus.ACCEPTED);

    }
}
