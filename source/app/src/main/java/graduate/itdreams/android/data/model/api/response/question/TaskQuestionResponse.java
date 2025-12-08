package graduate.itdreams.android.data.model.api.response.question;

import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import lombok.Data;

@Data
public class TaskQuestionResponse {
    private Long id;
    private String question;
    private String options;
    private int questionType;
    private SubTaskResponse task;
}
