package logixtek.docsoup.api.infrastructure.services.impl;

import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;


@Service
public class DefaultEmailSenderService implements EmailSenderService {

    private final String _smtpHost;

    private final String _smtpPort;

    private final String _smtpUsername;

    private final String _smtpPassword;

    private static final Properties props = System.getProperties();

    private static Session session;

    private final static Logger logger = LoggerFactory.getLogger(DefaultEmailSenderService.class);

    public DefaultEmailSenderService(@Value("${mail.smtp.host}") String smtpHost,
                                     @Value("${mail.smtp.port}" )  String smtpPort,
                                     @Value("${mail.smtp.username}" )  String smtpUsername,
                                     @Value("${mail.smtp.password}" )  String smtpPassword
    ) {
        _smtpHost = smtpHost;
        _smtpPort = smtpPort;
        _smtpUsername = smtpUsername;
        _smtpPassword = smtpPassword;
        initialEmailService();
    }


    private void initialEmailService() {
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", _smtpHost);
        props.put("mail.smtp.port", _smtpPort);
        props.put("mail.smtp.ssl.trust", _smtpHost);

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(_smtpUsername, _smtpPassword);
            }
        });
    }

    @Override
    @SneakyThrows
    public void sendHtmlMessage(String from, String recipient, String subject, String htmlBody) {
        var message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);
        message.setContent(htmlBody,"text/html");
        Transport.send(message);
    }

    @Override
    public void sendHtmlMessageWithAttachment(String from, String recipient, String subject, String htmlBody, File attachment) {
        try  {
            var message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody,"text/html");
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(attachment);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
