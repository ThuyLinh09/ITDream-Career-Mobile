package graduate.itdreams.android.data.model.api.request.login;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String idHash;
    private String otp;
    private String newPassword;
}
