package logixtek.docsoup.api.features.share.services.impl;

import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.infrastructure.constants.FeatureList;
import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import logixtek.docsoup.api.infrastructure.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DefaultDataRoomLimitationService implements DataRoomLimitationService {

    private final PlanTierRepository planTierRepository;

    private final FeatureFlagRepository featureFlagRepository;

    private final DataRoomContentRepository dataRoomContentRepository;

    private final CompanyRepository companyRepository;

    @Override
    public Boolean isAllow(UUID companyId, Long dataRoomId) {
       var planTierId = getPlanTierId(companyId);

       if(planTierId == null){
           return false;
       }

        var features = featureFlagRepository.findAllByPlanTierId(planTierId);

        var totalAssetFeature = features.stream()
                .filter(i -> Objects.equals(i.getFeatureKey(), FeatureList.TotalAssetsInSpace))
                .findAny()
                .orElse(null);

        var totalAssetInCompanyDataRoom = dataRoomContentRepository.countAllDataRoomContentByCompanyId(companyId.toString());

        return totalAssetFeature != null && (
                Objects.equals(totalAssetFeature.getLimit(), FeatureList.Unlimited) ||
                totalAssetFeature.getLimit() > totalAssetInCompanyDataRoom);
    }

    @Override
    public Boolean isAllow(UUID companyId) {
        var planTierId = getPlanTierId(companyId);

        if(planTierId == null){
            return false;
        }

        var features = featureFlagRepository.findAllByPlanTierId(planTierId);

        var totalAssetFeature = features.stream()
                .filter(i -> Objects.equals(i.getFeatureKey(), FeatureList.TotalAssetsInSpace))
                .findAny()
                .orElse(null);

        return totalAssetFeature != null && totalAssetFeature.getLimit() != FeatureList.Forbidden;
    }

    @Override
    public Boolean isDuplicate(UUID companyId, Long duplicatedDataRoomId) {
        var planTierId = getPlanTierId(companyId);

        if(planTierId == null){
            return false;
        }

        var features = featureFlagRepository.findAllByPlanTierId(planTierId);

        var totalAssetFeature = features.stream()
                .filter(i -> Objects.equals(i.getFeatureKey(), FeatureList.TotalAssetsInSpace))
                .findAny()
                .orElse(null);

        var totalAssetInDataRoom = dataRoomContentRepository.countAllContentByDataRoomId(duplicatedDataRoomId);

        var totalAssetInCompanyDataRoom = dataRoomContentRepository.countAllDataRoomContentByCompanyId(companyId.toString());

        return totalAssetFeature != null &&
                (totalAssetFeature.getLimit() == FeatureList.Unlimited
                        || totalAssetFeature.getLimit() > totalAssetInCompanyDataRoom + totalAssetInDataRoom);
    }

    private Long getPlanTierId(UUID companyId){
        var companyOption = companyRepository.findById(companyId);
        if(companyOption.isEmpty()){
            return null;
        }
        var planTierId = companyOption.get().getPlanTierId();

        var planTierOption = planTierRepository.findByIdAndIsActiveIsTrue(planTierId);

        return planTierOption.map(PlanTierEntity::getId).orElse(null);

    }
}
