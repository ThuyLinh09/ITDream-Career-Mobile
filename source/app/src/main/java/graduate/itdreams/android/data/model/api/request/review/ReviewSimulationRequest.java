package graduate.itdreams.android.data.model.api.request.review;

import lombok.Data;

@Data
public class ReviewSimulationRequest {
    private Long simulationId;
    private String comment;
    private int star;
}
