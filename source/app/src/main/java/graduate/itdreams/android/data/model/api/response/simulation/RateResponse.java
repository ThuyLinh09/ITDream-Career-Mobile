package graduate.itdreams.android.data.model.api.response.simulation;

import graduate.itdreams.android.data.model.api.response.account.AccountResponse;
import graduate.itdreams.android.data.model.api.response.student.StudentResponse;
import lombok.Data;

@Data
public class RateResponse {
    private Long id;
    private String comment;
    private AccountResponse student;
    private int star;
}
