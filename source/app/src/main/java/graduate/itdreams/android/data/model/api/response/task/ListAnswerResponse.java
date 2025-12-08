package graduate.itdreams.android.data.model.api.response.task;

import graduate.itdreams.android.data.model.api.response.question.TaskQuestionResponse;
import lombok.Data;

@Data
public class ListAnswerResponse {
    private Long id;
    private SubTaskProgressResponse studentSubTaskProgress;
    private TaskQuestionResponse taskQuestion;
    private String answer;
    private Boolean isCorrect;
}
