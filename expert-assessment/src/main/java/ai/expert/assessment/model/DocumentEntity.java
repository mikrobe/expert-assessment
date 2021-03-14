package ai.expert.assessment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="document")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DocumentEntity extends BaseEntity {

	public DocumentEntity(String name, String filepath, String contentType, long size) {
		this.name = name;
		this.filepath = filepath;
		this.contentType = contentType;
		this.size = size;
	}
	
	@Column(nullable = false, unique = true, length = 255)
	private String name;
	
	@Column(nullable = false, unique = true, length = 2048)
	private String filepath;
	
	@Column(length = 50)
	private String contentType;
	
	@Column()
	private long size;

//	@OneToMany(mappedBy = "document", targetEntity = CategorizationEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	private List<CategorizationEntity> categorizations = new ArrayList<>();
}