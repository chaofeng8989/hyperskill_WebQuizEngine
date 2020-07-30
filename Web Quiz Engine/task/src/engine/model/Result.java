package engine.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Result {
    boolean success;
    String feedback;
}
