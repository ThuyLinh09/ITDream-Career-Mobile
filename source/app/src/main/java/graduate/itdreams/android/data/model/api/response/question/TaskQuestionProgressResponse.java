package graduate.itdreams.android.data.model.api.response.question;

import graduate.itdreams.android.data.model.api.response.task.SubTaskProgressResponse;
import lombok.Data;

@Data
public class TaskQuestionProgressResponse {
    private Long id;
    private SubTaskProgressResponse studentSubTaskProgress;
    private TaskQuestionResponse taskQuestion;
    private String answer;
    private Boolean isCorrect;
}
