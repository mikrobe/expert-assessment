package ai.expert.assessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.expert.assessment.model.CategorizationEntity;
import lombok.NonNull;

@Repository
public interface CategorizationRepository extends JpaRepository<CategorizationEntity, Long>{

	List<CategorizationEntity> findByDocumentUid(@NonNull Long uid);
	
	void deleteByDocumentUid(@NonNull Long uid);
}
