package logixtek.docsoup.api.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "IX_DIRECTORY_ENTITY_PARENTID",  columnList="parentId", unique = false),
        @Index(name = "IX_DIRECTORY_ENTITY_NAME", columnList="name", unique = false),
        @Index(name = "IX_DIRECTORY_ENTITY_COMPANYID", columnList="companyId", unique = false),
        @Index(name = "IX_DIRECTORY_ENTITY_COMPANYID_ACCOUNTID", columnList="companyId,accountId", unique = false),
        @Index(name = "IX_DIRECTORY_ENTITY_CREATED_BY_COMPANY_ID",  columnList="createdBy,companyId"),
        @Index(name = "IX_DIRECTORY_ENTITY_MODIFIED_BY_COMPANY_ID",  columnList="modifiedBy,companyId")

})

@NamedStoredProcedureQuery(name = "DirectoryEntity.addWithParent", procedureName = "ins_directory_withParent", parameters = {
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "name", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "parentId", type = Long.class),
    @StoredProcedureParameter(mode=ParameterMode.IN, name = "accountId",type = String.class),
        @StoredProcedureParameter(mode=ParameterMode.IN, name = "companyId",type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT,name = "newId",type = Long.class),

})

@NamedStoredProcedureQuery(name = "DirectoryEntity.moveDirectory", procedureName = "move_directory", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "id", type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "parentId", type = Long.class),
        @StoredProcedureParameter(mode=ParameterMode.IN, name = "companyId",type = String.class),
        @StoredProcedureParameter(mode=ParameterMode.IN, name = "accountId",type = String.class),
        @StoredProcedureParameter(mode=ParameterMode.IN, name = "isTeam",type = Boolean.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT,name = "error",type = Integer.class)
})

@NamedStoredProcedureQuery(name = "DirectoryEntity.selChildrenOfParentId", procedureName = "sel_children_of_parentId", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "parentId", type = Long.class)
})

@NamedStoredProcedureQuery(name = "DirectoryEntity.selSecureIdsInDirectory",
        procedureName = "sel_secure_id_in_directory", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "directoryId", type = Long.class),
})

@NamedStoredProcedureQuery(name = "DirectoryEntity.delete", procedureName = "del_directory", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "directoryId", type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "errorCode", type = Integer.class)

})
@NamedStoredProcedureQuery(name = "DirectoryEntity.selDirectoriesByKeyword",
        procedureName = "sel_directory_by_keyword",
        parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "keyword", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "companyId", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "accountId", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "page", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "pageSize", type = Integer.class),
})
public class DirectoryEntity extends BaseAuditEntity implements CompanyOwnerInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private  long parentId = 0;

    @Column(nullable = false)
    private  int level =0;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    String name;


    @Column(length = 36,nullable = false)
    String accountId;

    @Column(nullable = false)
    @Type(type = "uuid-char")
    UUID companyId;

    @Column(nullable = false)
    Boolean isTeam = false;
}
