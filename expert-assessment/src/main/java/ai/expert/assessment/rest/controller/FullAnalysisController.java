package ai.expert.assessment.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ai.expert.assessment.facade.FullAnalysis;
import ai.expert.assessment.mapper.FullAnalysisMapper;
import ai.expert.assessment.service.AnalyzeSuccess;
import ai.expert.assessment.service.FullAnalysisService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class FullAnalysisController {

	private final FullAnalysisService service;
	private final FullAnalysisMapper mapper;
	
	public FullAnalysisController (FullAnalysisService service,
			FullAnalysisMapper mapper){
			this.service = service;
			this.mapper = mapper;
	}

	/**
	 * Gets FullAnalysis by document id.
	 *
	 * @param documentId the document id
	 * @return the FullAnalysis by the document
	 */
	@CrossOrigin
	@GetMapping("/fullanalysis/document/{id}")
	public ResponseEntity<List<FullAnalysis>> getFullAnalysisByDocumentId(@PathVariable(value = "id") Long documentUid){
		List<FullAnalysis> fullAnalysis = service.getFullAnalysisListByDocumentUid(documentUid);
		return ResponseEntity.ok().body(fullAnalysis);
	}
	
	@CrossOrigin
	@GetMapping("/fullanalysis/document/{id}/process")
	public ResponseEntity<Long> runFullAnalysisByDocumentId(@PathVariable(value = "id") Long documentUid, RedirectAttributes redirectAttributes){
		 List<AnalyzeSuccess> results = service.analyze(documentUid);
		String message = results.stream().map(r -> r.getAnalysisType() + ":" + (r.isSuccess() ? "success" : "failed")).collect(Collectors.joining("; "));
		redirectAttributes.addFlashAttribute("message", message);
		return ResponseEntity.created(URI.create("/fullanalysis/document/" + documentUid)).body(documentUid);
	}

}
