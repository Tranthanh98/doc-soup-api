package logixtek.docsoup.api.features.share.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ContentLimitationService {

    Boolean isAllowWithPageNumber(UUID companyId, String accountId, MultipartFile file);

    Boolean canUpdate(UUID companyId, String accountId, MultipartFile file);
}
