package logixtek.docsoup.api.infrastructure.thirdparty.models;

import lombok.Data;

@Data
public class DocumentInfo {
    String alink;
    String createdAt;
    Boolean crypted;
    Boolean deleted;
    String docName;
    String externalId;
    Long fileSize;
    String givenName;
    Boolean hasPassword;
    Boolean isEmptyDocName;
    String secureId;
    String streamdocsId;
    String type;
    String updatedAt;
    Boolean originExists;
}
