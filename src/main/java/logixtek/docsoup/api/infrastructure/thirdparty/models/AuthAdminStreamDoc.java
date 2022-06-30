package logixtek.docsoup.api.infrastructure.thirdparty.models;

import lombok.Data;

@Data
public class AuthAdminStreamDoc {

    String accessToken;

    String refreshToken;

    StreamDocUserInfo userInfo;

}
