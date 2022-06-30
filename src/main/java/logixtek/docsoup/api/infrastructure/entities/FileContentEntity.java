package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper = true)
@Table( name = "file_content",
        indexes = {
                @Index(name = "IX_FILE_CONTENT_CREATED_BY",  columnList="createdBy"),
                @Index(name = "IX_FILE_CONTENT_MODIFIED_BY",  columnList="modifiedBy")
        })
public class FileContentEntity extends BaseAuditEntity {
    @Id
    Long id;// this is fileId

    @Column(nullable = false, columnDefinition="BLOB")
    @Lob
    Blob content;

    public static FileContentEntity of(Long fileId, Blob fileContent, String accountId) {

        var result = new FileContentEntity();
        result.setId(fileId);
        result.setContent(fileContent);
        result.setCreatedBy(accountId);

        return  result;
    }
}
