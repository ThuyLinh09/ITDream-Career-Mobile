package graduate.itdreams.android.data.model.api.response.notification;

import graduate.itdreams.android.data.model.api.response.educator.EducatorResponse;
import lombok.Data;

@Data
public class NotificationResponse {
    private Long id;
    private String createdDate;
    private String message;
    private Boolean readFlag;
    private Long refId;
    private String refType;
    private String title;
}
