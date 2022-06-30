package logixtek.docsoup.api.features.dataroom.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(staticName = "of")
public class DataRoomDetail {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Integer viewType;

    @Getter
    @Setter
    private String ownerFullName;

    @Getter
    @Setter
    private String ownerEmail;

    @Getter
    @Setter
    private boolean isDisabledAllLink;

    @Getter
    @Setter
    private UUID companyId;

    @Getter
    @Setter
    private String accountId;

    @Getter
    @Setter
    @Nullable
    private List<DataRoomContentDirectory> directories;

    @Getter
    @Setter
    @Nullable
    private List<DataRoomContentFile> files;
}
