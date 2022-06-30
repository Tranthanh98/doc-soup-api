package logixtek.docsoup.api.features.link.linkAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.queries.GetListDataRoomOfLinkAccount;
import logixtek.docsoup.api.infrastructure.models.DataRoomInfo;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component("GetListDataRoomOfLinkAccountHandler")
@AllArgsConstructor
public class GetListDataRoomOfLinkAccountHandler implements Command.Handler<GetListDataRoomOfLinkAccount, ResponseMessageOf<Collection<DataRoomInfo>>> {
    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseMessageOf<Collection<DataRoomInfo>> handle(GetListDataRoomOfLinkAccount query) {
        var linkAccountOption = linkAccountsRepository.findById(query.getLinkAccountId());
        if(linkAccountOption.isPresent()) {
            var linkAccount = linkAccountOption.get();
            if(!linkAccount.getCompanyId().equals(query.getCompanyId())) {
                return ResponseMessageOf.ofBadRequest(ResponseResource.DonNotHavePermission, Map.of());
            }

            var result = linkAccountsRepository.getListDataRoomOfLinkAccount(linkAccount.getId());

            return ResponseMessageOf.of(HttpStatus.OK, result);
        }
        return ResponseMessageOf.ofBadRequest(ResponseResource.NotFoundLinkAccount, Map.of());
    }
}
