package ai.expert.assessment.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Provides file information in terms of filename, content and type
 * <p>
 * Used in {@link DocumentController}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDocument {
	@NonNull
	private String name;
	@NonNull
	private String content;
	private String contentType;
}
