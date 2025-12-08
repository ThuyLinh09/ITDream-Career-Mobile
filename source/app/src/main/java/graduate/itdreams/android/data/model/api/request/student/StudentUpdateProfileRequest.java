package graduate.itdreams.android.data.model.api.request.student;

import lombok.Data;

@Data
public class StudentUpdateProfileRequest {
    private String avatarPath;
    private String fullName;
    private String birthday;
    private String username;
}
