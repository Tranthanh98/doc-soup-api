package logixtek.docsoup.api.infrastructure.services;

import java.io.File;

public interface EmailSenderService {
     void sendHtmlMessage(String from, String recipient, String subject, String htmlBody);

     void sendHtmlMessageWithAttachment(String from, String recipient, String subject, String htmlBody, File attachment);
}
