package logixtek.docsoup.api.infrastructure.entities;

import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@MappedSuperclass
@Data
public abstract class BaseAuditEntity
{

    @Column(nullable = false)
    protected OffsetDateTime createdDate = OffsetDateTime.now(ZoneOffset.UTC);

    protected OffsetDateTime modifiedDate;

    @Column(length = 36,nullable = false)
    protected String createdBy;


    @Column(length = 36)
    protected String modifiedBy;

    @PreUpdate
    protected void onUpdate() {

        modifiedDate = OffsetDateTime.now(ZoneOffset.UTC);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            modifiedBy = authentication.getName();
        }
    }

    @PrePersist
    protected void onCreated(){
        createdDate = OffsetDateTime.now(ZoneOffset.UTC);

        if(createdBy == null || createdBy.isBlank()){
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication != null){
                createdBy = authentication.getName();
            }
        }

    }


}