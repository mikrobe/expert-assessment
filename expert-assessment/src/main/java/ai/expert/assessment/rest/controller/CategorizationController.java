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
import ai.expert.assessment.service.CategorizationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CategorizationController {

	private final CategorizationService service;
	
	public CategorizationController (CategorizationService service){
			this.service = service;
	}

	/**
	 * Gets Categorizations by document id.
	 *
	 * @param documentUid the document id
	 * @return the Categorizations
	 */
	@CrossOrigin
	@GetMapping("/categorizations/document/{id}")
	public ResponseEntity<List<Categorization>> getCategorizationByDocumentId(@PathVariable(value = "id") Long documentUid){
		List<Categorization> categorizations = service.getCategorizationByDocumentUid(documentUid);
		return ResponseEntity.ok().body(categorizations);
	}
	
	/**
	 * Starts categorization of the document
	 * @param documentUid the document id
	 * @param redirectAttributes
	 * @return the document id and the URI to the document categorization details
	 */
	@CrossOrigin
	@GetMapping("/categorizations/document/{id}/process")
	public ResponseEntity<Long> runCategorizationByDocumentId(@PathVariable(value = "id") Long documentUid, RedirectAttributes redirectAttributes){
		boolean success = service.categorize(documentUid);
		String message = success ? "Successfully categorized" : "Unsuccessfully categorized";
		redirectAttributes.addFlashAttribute("message", message);
		return ResponseEntity.created(URI.create("/categorizations/document/" + documentUid)).body(documentUid);
	}

}
