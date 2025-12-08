package graduate.itdreams.android.data.model.api.response.simulation;

import graduate.itdreams.android.data.model.api.response.educator.EducatorResponse;
import graduate.itdreams.android.data.model.api.response.specialization.SpecializationResponse;
import lombok.Data;

@Data
public class SimulationDetailResponse {
    private Float avgRating;
    private String description;
    private EducatorResponse educator;
    private String imagePath;
    private int level;
    private String overview;
    private Long participantQuantity;
    private SpecializationResponse specialization;
    private String title;
    private String totalEstimatedTime;
    private String videoPath;
    private Float percent;
}
