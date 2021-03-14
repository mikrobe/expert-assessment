package ai.expert.assessment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="categorization")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CategorizationEntity extends BaseEntity {
	
//	@ManyToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "document_uid", nullable = false)
//	private DocumentEntity document;
	
	@Column(name="document_uid")
	private Long documentUid;
	
	@Column(columnDefinition = "JSON")
	private String response;
}
