package ai.expert.assessment.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.util.media.Media;

import ai.expert.assessment.command.AddDocument;
import ai.expert.assessment.facade.Categorization;
import ai.expert.assessment.facade.Document;
import ai.expert.assessment.facade.FullAnalysis;
import ai.expert.assessment.service.AnalyzeSuccess;
import ai.expert.assessment.service.CategorizationService;
import ai.expert.assessment.service.DocumentService;
import ai.expert.assessment.service.FullAnalysisService;
import lombok.NonNull;

/**
 * The frontend service, decoupling the UI viewmodels from the backend services.
 * <p>
 * The UI part can be moved to an external war/server and in that case the frontend service communicates with the backend via REST api.
 * 
 * @author wsandri
 *
 */
@Service
public class FrontendService {
	
	@Autowired
	DocumentService documentService;
	@Autowired
	CategorizationService categorizationService;
	@Autowired
	FullAnalysisService fullAnalysisService;

	/**
	 * Returns the list of stored documents
	 * @return
	 */
	public List<Document> getDocuments() {
		return documentService.getDocuments();
	};
	
	/**
	 * Receives the uploaded files and it store them.
	 * @param medias
	 * @return
	 */
	public List<Document> saveDocuments(Media[] medias) {
		List<Document> documents = new ArrayList<>();
		if(medias != null) {
			for(Media m : medias) {
				if(documentService != null) {
					byte[] content = m.getByteData();
					long size = content.length;
					AddDocument command = new AddDocument(m.getName(), content, size, m.getContentType());
					Document document = documentService.addDocument(command);
					if(document != null) {
						documents.add(document);
					}
				}
			}
		}
		return documents;
	}
	
	/**
	 * 
	 * @param document
	 */
	public void deleteDocument(Document document) {
		documentService.delete(document);
		
	}
	
	/**
	 * 
	 * @param document
	 * @return
	 */
	public boolean categorize(Document document) {
		return categorizationService.categorize(document);
	}
	
	/**
	 * 
	 * @param document
	 * @return
	 */
	public List<AnalyzeSuccess> analyze(Document document) {
		return fullAnalysisService.analyze(document);
	}
	
	/**
	 * 
	 * @param documentUid
	 * @return
	 */
	public Categorization getCategorization(Long documentUid) {
		List<Categorization> categorizations = categorizationService.getCategorizationByDocumentUid(documentUid);
		return categorizations != null ? categorizations.stream().findFirst().orElse(null) : null;
	}
	
	/**
	 * 
	 * @param documentUid
	 * @return
	 */
	public List<FullAnalysis> getFullAnalysis(Long documentUid) {
		List<FullAnalysis> analysisList = fullAnalysisService.getFullAnalysisListByDocumentUid(documentUid);
		return analysisList != null ? analysisList : null;
	}
	
	/**
	 * 
	 * @param documentUid
	 * @return
	 */
	public Document findDocument(long documentUid) {
		return documentService.findDocument(documentUid);
	}
	
	/**
	 * 
	 * @param uid
	 * @return
	 */
	public byte[] getDocumentContent(@NonNull Long uid) {
		byte[] content = documentService.getDocumentContent(uid);
		return content != null ? content : new byte[0];
	}
			 
}
