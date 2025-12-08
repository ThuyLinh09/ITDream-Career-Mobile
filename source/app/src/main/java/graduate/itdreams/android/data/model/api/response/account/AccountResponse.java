package graduate.itdreams.android.data.model.api.response.account;

import lombok.Data;

@Data
public class AccountResponse {
    private String birthday;
    private ProfileAccountResponse profileAccountDto;
}
