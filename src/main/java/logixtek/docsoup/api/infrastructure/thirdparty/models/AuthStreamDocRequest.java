package logixtek.docsoup.api.infrastructure.thirdparty.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthStreamDocRequest {
    String userId;
    String userPw;
}
