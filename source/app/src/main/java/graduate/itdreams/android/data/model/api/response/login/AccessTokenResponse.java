package graduate.itdreams.android.data.model.api.response.login;

import lombok.Data;

@Data
public class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private long expires_in;
    private String scope;
    private int user_kind;
    private String tenant_info;
    private long user_id;
    private String grant_type;
    private String additional_info;
    private String jti;
}

