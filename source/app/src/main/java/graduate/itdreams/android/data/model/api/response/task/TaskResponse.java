package graduate.itdreams.android.data.model.api.response.task;

import java.util.List;

import graduate.itdreams.android.data.model.api.response.simulation.SimulationDetailResponse;
import lombok.Data;

@Data
public class TaskResponse {
    private Long id;
    private String content;
    private String description;
    private String introduction;
    private String name;
    private SimulationDetailResponse simulation;
    private String title;
    private int kind;
    private List<SubTaskResponse> subTasks;
    private TaskResponse parent;
}
