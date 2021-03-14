package ai.expert.assessment.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Provides file information in terms of filename, content, size and type
 * <p>
 * Used in {@link DocumentService}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDocument {
	@NonNull
	private String name;
	@NonNull
	private byte[] content;
	private long size;
	private String contentType;
}
