package graduate.itdreams.android.data.model.api.request.login;

import lombok.Data;

@Data
public class CandidateLoginRequest {
    private String email;
    private String password;
    private String grant_type = "student";
}
