package ai.expert.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.expert.assessment.model.DocumentEntity;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long>{

}
