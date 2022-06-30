package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.MakeUserOwner;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("MakeUserOwnerHandler")
@AllArgsConstructor
public class MakeUserOwnerHandler implements Command.Handler<MakeUserOwner, ResponseEntity<String>> {

    private final CompanyUserCacheService companyUserService;
    private final CompanyService companyService;
    @Override
    public ResponseEntity<String> handle(MakeUserOwner command) {

        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseEntity.badRequest().body(companyOption.getMessage());
        }

        var ownerCompanyUser = companyUserService.get(command.getAccountId(), command.getCompanyId());

        if(!ownerCompanyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseResource.DonNotHavePermission);
        }

        var targetCompanyUser = companyUserService.get(command.getTargetAccountId(), command.getCompanyId());

        if(targetCompanyUser == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found user");
        }

        if(targetCompanyUser.getMember_type() == CompanyUserConstant.OWNER_TYPE){
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        targetCompanyUser.setMember_type(CompanyUserConstant.OWNER_TYPE);
        targetCompanyUser.setRole(RoleDefinition.C_ADMIN);
        
        ownerCompanyUser.setMember_type(CompanyUserConstant.INVITED_TYPE);

        companyUserService.update(targetCompanyUser);
        companyUserService.update(ownerCompanyUser);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
