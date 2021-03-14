package ai.expert.assessment.model;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class DocumentPositionEntity {

    private Long start;
    
    private Long end;
}
