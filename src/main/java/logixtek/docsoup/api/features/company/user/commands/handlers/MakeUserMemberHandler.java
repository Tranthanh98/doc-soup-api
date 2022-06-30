package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.MakeUserMember;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("MakeUserMemberHandler")
@AllArgsConstructor
public class MakeUserMemberHandler implements Command.Handler<MakeUserMember, ResponseEntity<String>> {

    private final CompanyUserCacheService companyUserService;
    private final CompanyService companyService;

    @Override
    public ResponseEntity<String> handle(MakeUserMember command) {

        if(command.getAccountId().equals(command.getUserId())){
            return ResponseEntity.badRequest().body("You have not right privileges to perform this action");
        }

        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseEntity.badRequest().body(companyOption.getMessage());
        }

        var destinationUser = companyUserService.get(command.getUserId(), command.getCompanyId());

        if(destinationUser == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseResource.NotFoundUser);
        }

        if(destinationUser.getMember_type() == CompanyUserConstant.INVITED_TYPE && destinationUser.getRole().equals(RoleDefinition.C_MEMBER)){
            return ResponseEntity.accepted().build();
        }

        destinationUser.setMember_type(CompanyUserConstant.INVITED_TYPE);
        destinationUser.setRole(RoleDefinition.C_MEMBER);

        companyUserService.update(destinationUser);

        return ResponseEntity.accepted().build();
    }
}
