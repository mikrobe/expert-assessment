package ai.expert.assessment.service;

import ai.expert.assessment.nlapiclient.ExpertClient;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Holds the analysis execution result
 * @author wsandri
 *
 */
@Data
@AllArgsConstructor
public class AnalyzeSuccess {
	
	/** 
	 * the string value of {@link ExpertClient.ANALYSIS_TYPE}
	 */
	private String analysisType;
	
	private boolean success;
}
