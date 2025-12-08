package graduate.itdreams.android.data.model.api.response.simulation;

import graduate.itdreams.android.data.model.api.response.educator.EducatorResponse;
import lombok.Data;

@Data
public class AchievementResponse {
    private Long id;
    private String filePath;
    private SimulationResponse simulation;
    private String studentName;
}
