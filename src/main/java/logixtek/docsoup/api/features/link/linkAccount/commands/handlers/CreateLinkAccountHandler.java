package logixtek.docsoup.api.features.link.linkAccount.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.commands.CreateLinkAccount;
import logixtek.docsoup.api.features.link.linkAccount.mappers.LinkAccountMapper;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("CreateLinkAccountHandler")
@AllArgsConstructor
public class CreateLinkAccountHandler implements Command.Handler<CreateLinkAccount, ResponseEntity<Long>> {
    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<Long> handle(CreateLinkAccount command) {
        var existedLinkAccountOption = linkAccountsRepository.findByNameAndCompanyId(command.getName(), command.getCompanyId());
        if(existedLinkAccountOption.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(existedLinkAccountOption.get().getId());
        }

        var linkAccountEntity = LinkAccountMapper.INSTANCE.toEntity(command);

        var result = linkAccountsRepository.saveAndFlush(linkAccountEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getId());
    }
}
