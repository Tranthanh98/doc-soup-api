package logixtek.docsoup.api.features.content.responses;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AggregateContentViewModel {
    String name;
    Integer visits;
    Long size;
    OffsetDateTime createdDate;
    String ownerName;
    String createdBy;
    Boolean isFile = false;
    Long id;
    Long level;
    Boolean isTeam;
    Long parentId;
    Integer version;
}
