package ai.expert.assessment.facade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Document {
	@NonNull private Long uid;
	@NonNull private String name;
	private String contentType;
	private long size = 0;
	private String filepath;
}
