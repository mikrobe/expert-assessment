package ai.expert.assessment.facade;

import ai.expert.nlapi.v2.message.AnalyzeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullAnalysis {
	private Long uid;
	private Document document;
	private AnalyzeResponse analyzeResponse;
	private String type;
}
