package ai.expert.assessment.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.expert.assessment.facade.Document;
import ai.expert.assessment.facade.FullAnalysis;
import ai.expert.assessment.mapper.FullAnalysisMapper;
import ai.expert.assessment.model.FullAnalysisEntity;
import ai.expert.assessment.nlapiclient.ExpertClient;
import ai.expert.assessment.nlapiclient.ExpertClient.ANALYSIS_TYPE;
import ai.expert.assessment.repository.FullAnalysisRepository;
import ai.expert.nlapi.v2.cloud.Analyzer;
import ai.expert.nlapi.v2.message.AnalyzeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author wsandri
 *
 */
@Service
@Slf4j
@Transactional
public class FullAnalysisService {
	
	private final DocumentService documentService;
	private final FullAnalysisRepository fullAnalysisRepository;
	private final FullAnalysisMapper  fullAnalysisMapper;
	
	private final ExpertClient expertClient;
	
	@Autowired
	public FullAnalysisService(FullAnalysisRepository fullAnalysisRepository, 
								FullAnalysisMapper fullAnalysisMapper, 
								ExpertClient expertClient, 
								DocumentService documentService) {
		this.documentService = documentService;
		this.fullAnalysisRepository = fullAnalysisRepository;
		this.fullAnalysisMapper = fullAnalysisMapper;
		this.expertClient = expertClient;
	}
	
	/**
	 * Executes full analysis: disambiguation, relevants, entities and analyze
	 * @param document to analyze
	 * @return whether it succeed or not for each analysis
	 */
	public List<AnalyzeSuccess> analyze(Document document) {
		List<AnalyzeSuccess> results = new ArrayList<>();
		try {
			Analyzer analyzer = expertClient.createAnalyzer();
			File documentFile = new File(document.getFilepath());
			AnalyzeResponse disambiguationResponse = expertClient.analyze(documentFile, analyzer, ANALYSIS_TYPE.DISAMBIGUATION);
			AnalyzeResponse relevantsResponse = expertClient.analyze(documentFile, analyzer, ANALYSIS_TYPE.RELEVANTS);
			AnalyzeResponse entitiesResponse = expertClient.analyze(documentFile, analyzer, ANALYSIS_TYPE.ENTITIES);
			AnalyzeResponse analyzeResponse = expertClient.analyze(documentFile, analyzer, ANALYSIS_TYPE.ANALYZE);
			
			deletePreviousAnalysis(document.getUid());
			
			saveAnalyzeResponse(disambiguationResponse, document, ANALYSIS_TYPE.DISAMBIGUATION.getName());
			saveAnalyzeResponse(relevantsResponse, document, ANALYSIS_TYPE.RELEVANTS.getName());
			saveAnalyzeResponse(entitiesResponse, document, ANALYSIS_TYPE.ENTITIES.getName());
			saveAnalyzeResponse(analyzeResponse, document, ANALYSIS_TYPE.ANALYZE.getName());
			
			results.add(new AnalyzeSuccess(ANALYSIS_TYPE.DISAMBIGUATION.getName(), disambiguationResponse.isSuccess()));
			results.add(new AnalyzeSuccess(ANALYSIS_TYPE.RELEVANTS.getName(), relevantsResponse.isSuccess()));
			results.add(new AnalyzeSuccess(ANALYSIS_TYPE.ENTITIES.getName(), entitiesResponse.isSuccess()));
			results.add(new AnalyzeSuccess(ANALYSIS_TYPE.ANALYZE.getName(), analyzeResponse.isSuccess()));
			

		} catch (Exception e) {
			log.error("Analysing document {}: {}", document.getName(), e.getMessage());
		}
		return results;
	}
	
	/**
	 * Executes full analysis: disambiguation, relevants, entities and analyze
	 * @param documentUId the document id to analyze
	 * @return whether it succeed or not for each analysis
	 */
	public List<AnalyzeSuccess> analyze(Long documentUid) {
		return analyze(documentService.findDocument(documentUid));
	}

	/**
	 * 
	 * @param documentUid
	 */
	private void deletePreviousAnalysis(Long documentUid) {
		fullAnalysisRepository.deleteByDocumentUid(documentUid);
	}


	/**
	 * Saves the Analyis response to the repository, linking to the document.
	 * <p>
	 * The analysis response is saved as a JSON object into the fullanalysis table for the sake of simplicity and time.
	 * 
	 * @param categorizeResponse
	 * @param document
	 */
	private void saveAnalyzeResponse(AnalyzeResponse analyzeResponse, Document document, String type) {
		Long documentUid = document.getUid();
		if(documentService.exists(documentUid)) {
			FullAnalysisEntity fullAnalysisEntity = new FullAnalysisEntity();
			fullAnalysisEntity.setDocumentUid(documentUid);
			fullAnalysisEntity.setResponse(analyzeResponse.toJSON());
			fullAnalysisEntity.setType(type);
			saveFullAnalysis(fullAnalysisEntity);
		} else {
			log.error("Atempting to save a full analysis for a not existing Document with id {}", documentUid);
		}
	}

	/**
	 * 
	 * @param documentUid
	 * @return
	 */
	public List<FullAnalysis> getFullAnalysisListByDocumentUid(Long documentUid) {
		List<FullAnalysisEntity> fullAnalysisEntities = fullAnalysisRepository.findByDocumentUid(documentUid);
		return fullAnalysisMapper.entityToDtoList(fullAnalysisEntities);
	}
	
	/**
	 * 
	 * @param document
	 * @return
	 */
	public List<FullAnalysis> getFullAnalysisList(Document document) {
		return getFullAnalysisListByDocumentUid(document.getUid());
	}
	
	/**
	 * 
	 * @param fullAnalysisEntity
	 * @return
	 */
	private FullAnalysisEntity saveFullAnalysis(FullAnalysisEntity fullAnalysisEntity) {
		log.debug("Try to add new fullAnalysis execution {}.");
		FullAnalysisEntity savedEntity = this.fullAnalysisRepository.save(fullAnalysisEntity);
		log.debug("FullAnalysis {} saved to database. Created timestamp {}", savedEntity.getUid(), savedEntity.getDateCreated());
		return savedEntity;
	}
	
}