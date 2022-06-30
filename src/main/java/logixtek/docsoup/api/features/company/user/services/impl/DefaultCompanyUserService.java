package logixtek.docsoup.api.features.company.user.services.impl;

import com.google.common.base.Strings;
import logixtek.docsoup.api.features.company.user.services.CompanyUserService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultCompanyUserService implements CompanyUserService {
    private final EmailSenderService emailSenderService;
    private final EncryptService encryptService;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Value("${mail.from}")
    private String mailFrom;

    @Override
    @SneakyThrows
    public void inviteUser(CompanyUserEntity companyUser, String subject, String htmlTemplate, String senderName, Integer numberOfSend) {

        if(Strings.isNullOrEmpty(companyUser.getAccountId())) {
            var companyOption = companyRepository.findById(companyUser.getCompanyId());
            if(companyOption.isPresent()) {
                var classloader = Thread.currentThread().getContextClassLoader();
                InputStream is = classloader.getResourceAsStream(htmlTemplate);
                var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                IOUtils.closeQuietly(is);

                if(!Strings.isNullOrEmpty(htmlString)) {
                    var logoImageUrl = clientUrl + "/img/logo-black.png";
                    var emailImageUrl = clientUrl + "/img/invite-company-email.png";
                    var timestamp = Timestamp.valueOf(OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime());
                    var tokenString = timestamp.getTime() + "_" + companyUser.getId() + "_" + numberOfSend;
                    var token = encryptService.encrypt(tokenString);

                    companyUser.setToken(token);
                    companyUserRepository.saveAndFlush(companyUser);

                    var clientTokenString = companyUser.getEmail() + "_" + companyOption.get().getName();
                    var encoder = Base64.getEncoder();
                    var encodedString = encoder.encodeToString(clientTokenString.getBytes(StandardCharsets.UTF_8));

                    var inviteLink = clientUrl + "/accept-invitation/?token=" + token + "&clientToken=" + encodedString;
                    if(companyUser.getInvitationStatus().equals(CompanyUserConstant.REJECT_INVITATION)) {
                        inviteLink += "&accepted=true";
                    }

                    htmlString = htmlString
                            .replaceAll("@logoImage", logoImageUrl)
                            .replaceAll("@emailImage", emailImageUrl)
                            .replaceAll("@companyName", companyOption.get().getName())
                            .replaceAll("@senderName", senderName)
                            .replaceAll("@inviteLink", inviteLink);

                    emailSenderService.sendHtmlMessage(mailFrom, companyUser.getEmail(), subject, htmlString);
                }
            }
        }
    }
}
