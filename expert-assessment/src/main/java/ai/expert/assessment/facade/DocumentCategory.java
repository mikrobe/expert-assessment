package ai.expert.assessment.facade;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentCategory {

	private Long uid;
	private Long categorizationUid;
    private String id;
    private String label;
	private List<String> hierarchy;
    private int score;
    private Boolean winner;
    private String namespace;
    private Float frequency;
    private List<DocumentPosition> positions;
}
