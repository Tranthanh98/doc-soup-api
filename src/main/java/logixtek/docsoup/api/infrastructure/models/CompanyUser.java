package logixtek.docsoup.api.infrastructure.models;

public interface CompanyUser {
    Long getId();
    String getUserId();
    String getFullName();
    String getEmail();
    Integer getMember();
    Integer getStatus();
    String getRole();
}
