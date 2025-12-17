package graduate.itdreams.android.data.model.api.response.task;

import java.util.List;

import graduate.itdreams.android.data.model.api.response.simulation.SimulationDetailResponse;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import lombok.Data;

@Data
public class SubTaskResponse {
    private Long id;
    private String content;
    private String description;
    private String introduction;
    private String name;
    private String title;
    private int kind;
    private String filePath;
    private TaskResponse parent;
    private String videoPath;
}
