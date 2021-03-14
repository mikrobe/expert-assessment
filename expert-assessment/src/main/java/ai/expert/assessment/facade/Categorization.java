package ai.expert.assessment.facade;

import java.util.List;

import ai.expert.nlapi.v2.message.CategorizeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categorization {
	private Long uid;
	private Document document;
	private List<DocumentCategory> categories;
	private CategorizeResponse categorizeResponse;
}
