package graduate.itdreams.android.data.model.api.response.student;

import graduate.itdreams.android.data.model.api.response.account.AccountResponse;
import lombok.Data;

@Data
public class StudentResponse {
    private AccountResponse account;
    private String coverLetter;
    private Long id;
    private Boolean isAutoApply;
    private Boolean isJobSearching;
    private String jobTitle;
}
