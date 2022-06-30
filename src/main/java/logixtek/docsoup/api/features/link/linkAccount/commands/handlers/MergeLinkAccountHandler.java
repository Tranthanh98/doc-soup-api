package logixtek.docsoup.api.features.link.linkAccount.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.commands.MergeLinkAccount;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("MergeLinkAccountHandler")
@AllArgsConstructor
public class MergeLinkAccountHandler implements Command.Handler<MergeLinkAccount, ResponseMessageOf<String>> {
    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseMessageOf<String> handle(MergeLinkAccount command) {
        if(command.getSourceLinkAccountId().equals(command.getDestinationLinkAccountId())) {
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        var sourceLinkAccountOption = linkAccountsRepository.findById(command.getSourceLinkAccountId());
        var destinationLinkAccountOption = linkAccountsRepository.findById(command.getDestinationLinkAccountId());

        if(sourceLinkAccountOption.isPresent() && destinationLinkAccountOption.isPresent()) {
            var sourceLinkAccount = sourceLinkAccountOption.get();
            var destinationLinkAccount = destinationLinkAccountOption.get();

            if(!sourceLinkAccount.getCompanyId().equals(destinationLinkAccount.getCompanyId())) {
                return ResponseMessageOf.ofBadRequest(ResponseResource.OnlyMergeLinkAccountBelongToCompany, Map.of());
            }

            if(!sourceLinkAccount.getCompanyId().equals(command.getCompanyId())) {
                return ResponseMessageOf.ofBadRequest(ResponseResource.DonNotHavePermission, Map.of());
            }

            linkAccountsRepository.mergeLinkAccount(command.getSourceLinkAccountId(), command.getDestinationLinkAccountId());

            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return ResponseMessageOf.ofBadRequest(ResponseResource.NotFoundLinkAccount, Map.of());
    }
}
