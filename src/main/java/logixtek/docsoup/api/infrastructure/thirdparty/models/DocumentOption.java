package logixtek.docsoup.api.infrastructure.thirdparty.models;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@FieldNameConstants
public class DocumentOption {
    String docName;

    Boolean save;

    Boolean download;

    Boolean print;

    Boolean form;

    String lifeSpan;
}
