package logixtek.docsoup.api.features.administrator.checkin.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.checkin.commands.AdminCheckIn;
import logixtek.docsoup.api.infrastructure.entities.InternalAccountEntity;
import logixtek.docsoup.api.infrastructure.repositories.InternalAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component("AdminCheckInHandler")
@AllArgsConstructor
public class AdminCheckInHandler implements Command.Handler<AdminCheckIn, ResponseEntity<String>> {

    private final InternalAccountRepository repository;

    @Override
    public ResponseEntity<String> handle(AdminCheckIn command) {

        var accountFromDBOption = repository.findById(command.getInternalAccountId());

        if(accountFromDBOption.isPresent()){
            var account = accountFromDBOption.get();

            account.setCheckInTime(Instant.now());

            repository.saveAndFlush(account);

            return ResponseEntity.status(HttpStatus.OK).build();
        }

        var newAccount =InternalAccountEntity.of(command.getInternalAccountId(), command.getEmail(), Instant.now());

        repository.saveAndFlush(newAccount);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
