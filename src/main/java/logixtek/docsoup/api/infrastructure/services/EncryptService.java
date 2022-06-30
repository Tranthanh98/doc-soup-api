package logixtek.docsoup.api.infrastructure.services;

public interface EncryptService {
    String encrypt(String unencryptedString);
    String decrypt(String encryptedString);
}
