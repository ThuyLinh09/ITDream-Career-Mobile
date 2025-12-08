package graduate.itdreams.android.data.model.api.response.simulation;

import graduate.itdreams.android.data.model.api.response.educator.EducatorResponse;
import lombok.Data;

@Data
public class SimulationResponse {
    private Long id;
    private String title;
    private Float avgRating;
    private EducatorResponse educator;
    private String imagePath;
    private int level;
    private Float percent;
    private String totalEstimatedTime;
}
