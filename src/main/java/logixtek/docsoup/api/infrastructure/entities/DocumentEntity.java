package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "document",
        indexes = {
                @Index(name = "IX_DOCUMENT_REFID",  columnList="refId", unique = false),
                @Index(name = "IX_DOCUMENT_EXPIREDAT", columnList="expiredAt", unique = false),
                @Index(name = "IX_DOCUMENT_CREATED_BY",  columnList="createdBy")
        })
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class DocumentEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    UUID id;
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
    OffsetDateTime expiredAt;

    @Type(type = "uuid-char")
    UUID  refId;

    Long fileId;

    Integer fileVersion = 1;
}