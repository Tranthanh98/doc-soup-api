package logixtek.docsoup.api.features.share.services.impl;

import logixtek.docsoup.api.features.share.services.ContentLimitationService;
import logixtek.docsoup.api.infrastructure.constants.FeatureList;
import logixtek.docsoup.api.infrastructure.entities.FeatureFlagEntity;
import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.FeatureFlagRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DefaultContentLimitationService implements ContentLimitationService {

    private final CompanyRepository companyRepository;
    private final PlanTierRepository planTierRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final FileRepository fileRepository;

    private static final Logger logger = LoggerFactory.getLogger(DefaultContentLimitationService.class);

    @Override
    public Boolean isAllowWithPageNumber(UUID companyId, String accountId, MultipartFile file) {
        var planTierId = getPlanTierId(companyId);

        if(planTierId == null){
            return false;
        }

        var totalFileOfCompany = fileRepository.countAllByCompanyId(companyId);

        var features = featureFlagRepository.findAllByPlanTierId(planTierId);

        var totalContentSize = fileRepository.sumAllSizeByCompanyIdAndAccountId(companyId, accountId);

        if(totalContentSize == null){
            totalContentSize = 0L;
        }

        try{
            for (FeatureFlagEntity feature: features) {
                if(feature.getFeatureKey().equals(FeatureList.DocumentLimit) &&
                        feature.getLimit() != FeatureList.Unlimited &&
                        totalFileOfCompany >= feature.getLimit()){
                    return false;
                }

                if(feature.getFeatureKey().equals(FeatureList.IncludeStorage)){
                    if(feature.getLimit() == FeatureList.Forbidden){
                        return false;
                    }

                    var units = feature.getUnit().split("/");

                    var limitedStorage = Utils.convertSizeUnitToByteSize(feature.getLimit(), units[0]);

                    if(limitedStorage < totalContentSize){
                        return false;
                    }
                }
                if(feature.getFeatureKey().equals(FeatureList.DocumentPages) &&
                        feature.getLimit() != FeatureList.Unlimited &&
                        getNumberOfPage(file) >= feature.getLimit()){
                    return false;

                }

            }

            return true;
        }
        catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

    }

    @Override
    public Boolean canUpdate(UUID companyId, String accountId, MultipartFile file) {

        if(!isAllowWithPageNumber(companyId, accountId, file)){
            return false;
        }

        var planTierId = getPlanTierId(companyId);

        if(planTierId == null){
            return false;
        }

        var features = featureFlagRepository.findAllByPlanTierId(planTierId);

        var totalUsedStorage = fileRepository.sumAllSizeByCompanyIdAndAccountId(companyId, accountId);

        for (FeatureFlagEntity feature: features) {

            if(feature.getFeatureKey().equals(FeatureList.IncludeStorage)){
                if(feature.getLimit() == FeatureList.Forbidden){
                    return false;
                }

                var units = feature.getUnit().split("/");

                var limitedStorage = Utils.convertSizeUnitToByteSize(feature.getLimit(), units[0]);

                if(limitedStorage < totalUsedStorage + file.getSize()){
                    return false;
                }
            }

        }

        return true;
    }

    private int getNumberOfPage(MultipartFile file) throws IOException {
        PDDocument doc = Loader.loadPDF(file.getBytes());
        return doc.getNumberOfPages();
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
