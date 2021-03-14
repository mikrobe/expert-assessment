package ai.expert.assessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.expert.assessment.model.DocumentCategoryEntity;
import lombok.NonNull;

@Repository
public interface CategoryRepository extends JpaRepository<DocumentCategoryEntity, Long>{

	List<DocumentCategoryEntity> findByCategorizationUid(@NonNull Long uid);
}
