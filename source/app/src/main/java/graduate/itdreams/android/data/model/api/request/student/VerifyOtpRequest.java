package graduate.itdreams.android.data.model.api.request.student;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String idHash;
    private String otp;
}
