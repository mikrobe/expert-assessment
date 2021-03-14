package ai.expert.assessment.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Data;

/**
 * Basic Entity with shared attributes
 *
 */
@Data
@MappedSuperclass
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long uid;
	
	@Column(nullable = false, updatable = false)
	protected OffsetDateTime dateCreated;
	
	@Column(nullable = false)
	protected OffsetDateTime lastUpdated;
	
	@PrePersist
	protected void prePersist() {
		this.dateCreated = OffsetDateTime.now();
		this.lastUpdated = this.dateCreated;
	}
	
	@PreUpdate
	protected void preUpdate() {
		this.lastUpdated = OffsetDateTime.now();
	}
}
