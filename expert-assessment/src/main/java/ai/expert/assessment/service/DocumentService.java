package ai.expert.assessment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.expert.assessment.command.AddDocument;
import ai.expert.assessment.configuration.DocumentServiceConfig;
import ai.expert.assessment.facade.Document;
import ai.expert.assessment.mapper.DocumentMapper;
import ai.expert.assessment.model.DocumentEntity;
import ai.expert.assessment.repository.DocumentRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author wsandri
 *
 */
@Service
@Slf4j
@Transactional
public class DocumentService {
	
	private final DocumentServiceConfig config;
	private final DocumentRepository repository;
	private final DocumentMapper mapper;
	
	@Autowired
	public DocumentService(DocumentRepository repository, DocumentMapper mapper, DocumentServiceConfig config) {
		this.repository = repository;
		this.mapper = mapper;
		this.config = config;
	}

	/**
	 * Saves a new document to the repository and the content file to the filesystem
	 * @param command {@link AddDocument}
	 * @return
	 */
	public Document addDocument(AddDocument command) {
		log.debug("Try to add new document {}.", command.getName());
		
		DocumentEntity entity = this.mapper.commandToEntity(command);
		String filepath = saveDocumentToFS(config.getDocumentFolder(), String.format("%s-%s", UUID.randomUUID(), entity.getName()), command.getContent());
		if(filepath != null) {
			entity.setFilepath(filepath);
			DocumentEntity savedEntity = this.repository.save(entity);
			log.debug("Document {} saved to database. Created timestamp {}", savedEntity.getUid(), savedEntity.getDateCreated());
			return getDocument(savedEntity);
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public List<Document> getDocuments() {
		return this.repository.findAll()
				.stream().map(c -> this.mapper.entityToDto(c))
				.collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param document
	 */
	public void delete(@NonNull Document document) {
		this.repository.deleteById(document.getUid());
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Document findDocument(@NonNull Long id) {
		DocumentEntity documentEntity = this.repository.getOne(id);
		return getDocument(documentEntity);
	}
	
	/**
	 * Read document content from the filesystem
	 * @param id
	 * @return
	 */
	public byte[] getDocumentContent(@NonNull Long id) {
		byte[] content = null;
		Document document = findDocument(id);
		if(document != null) {
			try {
				content = Files.readAllBytes(Paths.get(document.getFilepath()));
			} catch (IOException e) {
				log.error("Error reading content from Document file [{}]", document.getFilepath() );
			}
		}
		return content;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean exists(@NonNull Long id) {
		return this.repository.existsById(id);
	}
	
	/**
	 * 
	 * @param documentFolder
	 * @param name
	 * @param content
	 * @return
	 */
	private String saveDocumentToFS(@NonNull String documentFolder, @NonNull String name, @NonNull byte[] content) {
		String filepath = null;
		try {
			Path folderPath = Files.createDirectories(Paths.get(documentFolder));
			filepath = Files.write(folderPath.resolve(name), content).toString();
        } catch (IOException e) {
            log.error("Failed to save document [{}] in [{}]: {}", documentFolder, e.getMessage());
        }
		return filepath;
	}

	private Document getDocument(DocumentEntity entity) {
		return mapper.entityToDto(entity);
	}
}