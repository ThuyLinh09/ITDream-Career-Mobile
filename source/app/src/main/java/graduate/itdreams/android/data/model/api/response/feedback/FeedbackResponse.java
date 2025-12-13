package graduate.itdreams.android.data.model.api.response.feedback;

import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import lombok.Data;

@Data
public class FeedbackResponse {
    private Long id;
    private String content;
    private SimulationResponse simulation;
    private String modifiedDate;
}
