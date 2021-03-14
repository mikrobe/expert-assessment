package ai.expert.assessment.model;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="category")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DocumentCategoryEntity extends BaseEntity {
//	@ManyToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "categorization_id", nullable = false)
//	private CategorizationEntity categorization;
	
	@Column(name="categorization_uid")
	private Long categorizationUid;
	
	private String id;
	
    private String label;
    
    @ElementCollection
    @CollectionTable(name = "category_hierarchy", joinColumns=@JoinColumn(name = "uid"))
    @Column(name="hierarchy")
    private List<String> hierarchy;
    
    private int score;
    
    private Boolean winner;
    
    private String namespace;
    
    private Float frequency;
    
    @ElementCollection
    @CollectionTable(name = "category_position", joinColumns = @JoinColumn(name = "uid"))
    private List<DocumentPositionEntity> positions;
}