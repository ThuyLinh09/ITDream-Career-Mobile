package graduate.itdreams.android.data.model.api.response.question;

import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import lombok.Data;

@Data
public class QuestionResponse {
    private int questionType;
    private String question;
    private String options;
    private Long taskId;
}
