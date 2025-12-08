package graduate.itdreams.android.data.model.api.request.task;

import lombok.Data;

@Data
public class TaskQuestionProgressRequest {
    private Long taskQuestionId;
    private Long studentSubTaskProgressId;
    private String answer;
    private Boolean isCorrect;
}
