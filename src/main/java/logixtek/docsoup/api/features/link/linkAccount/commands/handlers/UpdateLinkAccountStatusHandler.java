package logixtek.docsoup.api.features.link.linkAccount.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.commands.UpdateLinkAccountStatus;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("UpdateLinkAccountStatusHandler")
@AllArgsConstructor
public class UpdateLinkAccountStatusHandler implements Command.Handler<UpdateLinkAccountStatus, ResponseEntity<String>> {
    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<String> handle(UpdateLinkAccountStatus command) {
        var linkAccountOption = linkAccountsRepository.findById(command.getId());
        if(linkAccountOption.isPresent()) {
            var linkAccount = linkAccountOption.get();
            if(linkAccount.getCompanyId().equals(command.getCompanyId())) {
                if(linkAccount.getArchived().equals(command.getArchived())) {
                    return ResponseEntity.accepted().build();
                }
                
                linkAccount.setArchived(command.getArchived());
                linkAccountsRepository.saveAndFlush(linkAccount);

                return ResponseEntity.accepted().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }
}
