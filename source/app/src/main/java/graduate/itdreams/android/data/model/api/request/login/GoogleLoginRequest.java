package graduate.itdreams.android.data.model.api.request.login;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String accessToken;
    private String userRole = "student";
}
