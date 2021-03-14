package ai.expert.assessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.expert.assessment.model.FullAnalysisEntity;
import lombok.NonNull;

@Repository
public interface FullAnalysisRepository extends JpaRepository<FullAnalysisEntity, Long>{

	List<FullAnalysisEntity> findByDocumentUid(@NonNull Long uid);

	void deleteByDocumentUid(Long documentUid);

}
