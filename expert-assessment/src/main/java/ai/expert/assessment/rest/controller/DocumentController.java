package ai.expert.assessment.rest.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ai.expert.assessment.command.AddDocument;
import ai.expert.assessment.command.CreateDocument;
import ai.expert.assessment.facade.Document;
import ai.expert.assessment.service.DocumentService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DocumentController {

	private final DocumentService service;
	
	public DocumentController (DocumentService service){
			this.service = service;
	}
	
	/**
	 * Creates a document providing the filename and the file content as a String.
	 * <p>
	 * See {@link CreateDocument}
	 * @param command the document as a filename and a string content
	 * @return the document id and the URI to the document details
	 */
	@CrossOrigin
	@PostMapping(value = "/documents")
	ResponseEntity<Long> create(@RequestBody @Valid CreateDocument command) {
		log.info("Create new document request received. [name: {}]", command.getName());
		
		try {
			AddDocument addDocument = new AddDocument(command.getName(), command.getContent().getBytes(), command.getContent().length(), command.getContentType());
			Document document = service.addDocument(addDocument);
			return ResponseEntity.created(URI.create("/document/" + document.getUid().toString())).body(document.getUid());
		} catch (Exception e) {
			return internalErrorResponse(e);
		}
	}
	
	/**
	 * Upload a file and creates a new document
	 * @param file
	 * @param redirectAttributes
	 * @return the document id and the URI to the document details
	 */
	@CrossOrigin
	@PostMapping(value = "/documents/upload")
	ResponseEntity<Long> upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		log.info("Upload document request received.");
		
		try {
			byte[] content = file.getBytes();
			AddDocument command = new AddDocument(file.getOriginalFilename(), content, content.length, file.getContentType());
			log.info("Document uploaded [name: {}]", command.getName());
			Document document = service.addDocument(command);
			redirectAttributes.addFlashAttribute("message", "Successfully uploaded " + command.getName());
			return ResponseEntity.created(URI.create("/document/" + document.getUid().toString())).body(document.getUid());
		} catch (Exception e) {
			return internalErrorResponse(e);
		}
	}

	/**
	 * Gets the list of documents stored in the repository
	 * @return the list of documents
	 */
	@CrossOrigin
	@GetMapping(value = "/documents", produces = "application/json")
	public ResponseEntity<List<Document>> getDocuments() {
		log.info("Fetch all documents");
		
		try {
			List<Document> documents = service.getDocuments();
			return ResponseEntity.ok(documents);
		} catch (Exception e) {
			return internalErrorResponse(e);
		}
	}

	/**
	 * Gets document by id.
	 *
	 * @param documentUid the document id
	 * @return the document or a NOT_FOUND status
	 */
	@CrossOrigin
	@GetMapping("/documents/{id}")
	public ResponseEntity<Document> getDocumentById(@PathVariable(value = "id") Long documentUid){
		Document document = service.findDocument(documentUid);
		return document != null ? ResponseEntity.ok().body(document) : ResponseEntity.notFound().build();
	}

	/**
	 * Gets file content by document id.
	 *
	 * @param documentUid the document id
	 * @return the file content or a NOT_FOUND status
	 */
	@CrossOrigin
	@GetMapping("/documents/{id}/content")
	public ResponseEntity<String> getDocumentContentById(@PathVariable(value = "id") Long documentUid){
		byte[] content = service.getDocumentContent(documentUid);
		return content != null ? ResponseEntity.ok().body(new String(content)) : ResponseEntity.notFound().build();
	}

	private ResponseEntity internalErrorResponse(Exception e) {
		if(e != null) { log.error(e.getMessage()); }
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
