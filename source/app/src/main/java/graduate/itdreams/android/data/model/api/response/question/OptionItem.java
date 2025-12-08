package graduate.itdreams.android.data.model.api.response.question;

import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import lombok.Data;

@Data
public class OptionItem {
    private String option;
    private boolean answer;
    private boolean isSelected;
}
