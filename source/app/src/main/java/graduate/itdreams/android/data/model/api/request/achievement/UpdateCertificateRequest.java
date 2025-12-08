package graduate.itdreams.android.data.model.api.request.achievement;

import lombok.Data;

@Data
public class UpdateCertificateRequest {
    private String filePath;
    private Long id;
}
