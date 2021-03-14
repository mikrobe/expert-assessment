package ai.expert.assessment.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.expert.assessment.facade.Categorization;
import ai.expert.assessment.facade.Document;
import ai.expert.assessment.mapper.CategorizationMapper;
import ai.expert.assessment.mapper.CategoryMapper;
import ai.expert.assessment.model.CategorizationEntity;
import ai.expert.assessment.model.DocumentCategoryEntity;
import ai.expert.assessment.nlapiclient.ExpertClient;
import ai.expert.assessment.repository.CategorizationRepository;
import ai.expert.assessment.repository.CategoryRepository;
import ai.expert.nlapi.v2.cloud.Categorizer;
import ai.expert.nlapi.v2.message.CategorizeResponse;
import ai.expert.nlapi.v2.model.Category;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class CategorizationService {
	
	private final DocumentService documentService;
	private final CategorizationRepository categorizationRepository;
	private final CategorizationMapper  categorizationMapper;
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;
	
	private final ExpertClient expertClient;
	
	@Autowired
	public CategorizationService(CategorizationRepository categorizationRepository, 
								CategorizationMapper  categorizationMapper, 
								CategoryRepository categoryRepository, 
								CategoryMapper categoryMapper, 
								ExpertClient expertClient, 
								DocumentService documentService) {
		this.documentService = documentService;
		this.categorizationRepository = categorizationRepository;
		this.categorizationMapper = categorizationMapper;
		this.categoryRepository = categoryRepository;
		this.categoryMapper = categoryMapper;
		this.expertClient = expertClient;
	}

	/**
	 * Executes categorization
	 * @param document to categorize
	 * @return whether it succeed or not
	 */
	public boolean categorize(Document document) {
		boolean success = false;
		if(document != null) {
			try {
				Categorizer categorizer = expertClient.createCategorizer();
				File documentFile = new File(document.getFilepath());
				CategorizeResponse categorizeResponse = expertClient.categorize(documentFile, categorizer);
				saveCategorizeResponse(categorizeResponse, document);
				success = categorizeResponse.isSuccess();
			} catch (Exception e) {
				log.error("Analysing document {}: {}", document.getName(), e.getMessage());
			}
		}
		return success;
	}
	
	/**
	 * Executes categorization
	 * @param documentUid the document to be categorize
	 * @return whether it successed or not
	 */
	public boolean categorize(Long documentUid) {
		return categorize(documentService.findDocument(documentUid));
	}

	/**
	 * Saves the Categorization response to the repository, linking to the document.
	 * <p>
	 * The categorization response is saved as a JSON object into the Categorization table
	 * and as a list of categories (with its embedded collections) into the Category table.
	 * 
	 * @param categorizeResponse
	 * @param document
	 */
	private void saveCategorizeResponse(@NonNull CategorizeResponse categorizeResponse, @NonNull Document document) {
		Long documentUid = document.getUid();
		if(documentService.exists(documentUid)) {
			// cleanup the previous ones if any
			categorizationRepository.deleteByDocumentUid(documentUid);
			
			CategorizationEntity categorizationEntity = new CategorizationEntity();
			categorizationEntity.setDocumentUid(documentUid);
			categorizationEntity.setResponse(categorizeResponse.toJSON());
			CategorizationEntity savedCategorizationEntity = saveCategorization(categorizationEntity);
			
			Long savedCategorizationUid = savedCategorizationEntity.getUid();
			
			List<Category> categories = categorizeResponse.getData().getCategories();
			List<DocumentCategoryEntity> categoryEntities = categoryMapper.nlapiModelToEntityList(categories);
			for(DocumentCategoryEntity categoryEntity : categoryEntities) {
				categoryEntity.setCategorizationUid(savedCategorizationUid);
			}
			categoryRepository.saveAll(categoryEntities);
			
		}
	}
	
	/**
	 * 
	 * @param documentUid
	 * @return
	 */
	public List<Categorization> getCategorizationByDocumentUid(Long documentUid) {
		List<Categorization> categorizations = new ArrayList<>();
		List<CategorizationEntity> categorizationEntities = categorizationRepository.findByDocumentUid(documentUid);
		
		for(CategorizationEntity cz:categorizationEntities) {
			List<DocumentCategoryEntity> categories = categoryRepository.findByCategorizationUid(cz.getUid());
			categorizations.add(categorizationMapper.entityToDto(cz, categoryMapper.entityToDtoList(categories)));
		}
		return categorizations;
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	public List<Categorization> getCategorization(Document document) {
		return getCategorizationByDocumentUid(document.getUid());
	}
	
	/**
	 * Saves the categorization to the repository
	 * @param categorizationEntity
	 * @return the saved categorizationEntity
	 */
	private CategorizationEntity saveCategorization(CategorizationEntity categorizationEntity) {
		log.debug("Try to add new categorization execution {}.");
		
		CategorizationEntity savedEntity = this.categorizationRepository.save(categorizationEntity);
		log.debug("Categorization {} saved to database. Created timestamp {}", savedEntity.getUid(), savedEntity.getDateCreated());
		return savedEntity;
	}
}