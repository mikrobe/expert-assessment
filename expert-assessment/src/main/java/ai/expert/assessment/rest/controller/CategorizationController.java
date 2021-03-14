package ai.expert.assessment.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ai.expert.assessment.facade.Categorization;
import ai.expert.assessment.mapper.CategorizationMapper;
import ai.expert.assessment.service.CategorizationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CategorizationController {

	private final CategorizationService service;
	private final CategorizationMapper mapper;
	
	public CategorizationController (CategorizationService service,
			CategorizationMapper mapper){
			this.service = service;
			this.mapper = mapper;
	}

	/**
	 * Gets Categorizations by document id.
	 *
	 * @param documentId the document id
	 * @return the Categorizations by the document
	 */
	@CrossOrigin
	@GetMapping("/categorizations/document/{id}")
	public ResponseEntity<List<Categorization>> getCategorizationByDocumentId(@PathVariable(value = "id") Long documentUid){
		List<Categorization> categorizations = service.getCategorizationByDocumentUid(documentUid);
		return ResponseEntity.ok().body(categorizations);
	}
	
	@CrossOrigin
	@GetMapping("/categorizations/document/{id}/process")
	public ResponseEntity<Long> runCategorizationByDocumentId(@PathVariable(value = "id") Long documentUid, RedirectAttributes redirectAttributes){
		boolean success = service.categorize(documentUid);
		String message = success ? "Successfully categorized" : "Unsuccessfully categorized";
		redirectAttributes.addFlashAttribute("message", message);
		return ResponseEntity.created(URI.create("/categorizations/document/" + documentUid)).body(documentUid);
	}

}
