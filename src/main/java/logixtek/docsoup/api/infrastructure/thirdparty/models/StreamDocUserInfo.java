package logixtek.docsoup.api.infrastructure.thirdparty.models;

import lombok.Data;

@Data
class StreamDocUserInfo {
    Long createdAt;
    String name;
    Long id;
    String type;
    String userId;
    String email;
    Long updatedAt;
}
