package logixtek.docsoup.api.features.share.dtos;

import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
@Setter
public class SendEmailResetPasswordRequestMessage {
    AccountEntity account;
}
