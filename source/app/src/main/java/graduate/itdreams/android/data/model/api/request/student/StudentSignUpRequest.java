package graduate.itdreams.android.data.model.api.request.student;

import lombok.Data;

@Data
public class StudentSignUpRequest {
    private String email;
    private String fullName;
    private String password;
    private String phone;
    private String username;
    private String birthday;
}
