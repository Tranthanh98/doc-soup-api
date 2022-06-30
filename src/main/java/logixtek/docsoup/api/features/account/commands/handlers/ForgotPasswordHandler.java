package logixtek.docsoup.api.features.account.commands.handlers;

import an.awesome.pipelinr.Command;
import com.nimbusds.jose.shaded.json.JSONObject;
import logixtek.docsoup.api.features.account.commands.ForgotPassword;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailResetPasswordDomainEvent;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component("ForgotPasswordHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ForgotPasswordHandler implements Command.Handler<ForgotPassword, ResponseMessageOf<String>> {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordHandler.class);
    private final AccountRepository accountRepository;
    private final EncryptService encryptService;
    private final JobMessageQueuePublisher publisher;

    @Override
    public ResponseMessageOf<String> handle(ForgotPassword command) {

        var userOption = accountRepository.findById(command.getAccountId());

        if (!userOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND, ResponseResource.NotFoundUser);
        }

        var user = userOption.get();

        var timestamp = Timestamp.valueOf(OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        var jsonObject = new JSONObject();
        jsonObject.put("createdDate", timestamp.getTime());
        jsonObject.put("accountId", command.getAccountId());
        var token = encryptService.encrypt(jsonObject.toJSONString());

        user.setToken(token);

        var accountResult = accountRepository.saveAndFlush(user);

        var jobMessage = new JobMessage<SendEmailResetPasswordDomainEvent>();
        jobMessage.setAction(JobActionConstant.SEND_EMAIL_RESET_PASSWORD);
        jobMessage.setObjectName(SendEmailResetPasswordDomainEvent.class.getName());
        var body = SendEmailResetPasswordDomainEvent.of(accountResult);
        jobMessage.setDataBody(body);

        try {
            publisher.sendMessage(jobMessage);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ResponseMessageOf.of(HttpStatus.OK);

    }
}
