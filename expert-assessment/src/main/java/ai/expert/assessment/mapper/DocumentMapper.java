package ai.expert.assessment.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import ai.expert.assessment.command.AddDocument;
import ai.expert.assessment.facade.Document;
import ai.expert.assessment.model.DocumentEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to convert documents between facade (DTO) and entity
 */
@Component
@Slf4j
@AllArgsConstructor
public class DocumentMapper {
	
	private final ModelMapper modelMapper;
	
	/**
	 * Converts the new document request to entity.
	 * @param command Contain document information such as filename, content, size and contentType
	 * @return the Document entity
	 */
	public DocumentEntity commandToEntity(AddDocument command) {
		log.debug("Convert 'AddDocument' command to new document entity instance, ['filename': {}]",
				command.getName());
		
		DocumentEntity entity = modelMapper.map(command, DocumentEntity.class);
		
		log.debug("Document entity {} with id {} initialized.", entity.getName(), entity.getUid());
		return entity;
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public Document entityToDto(DocumentEntity entity) {
		Document dto = null;
		if(entity != null) {
			log.debug("Convert 'Document' entity to DTO. ['id' {}, 'title', {}]", entity.getUid(), entity.getName());
			dto = modelMapper.map(entity, Document.class);
			if(dto != null) {
				log.debug("DTO '{}' initialized with id {}", dto.getName(), dto.getUid());
			}
		}
		return dto;
	}
}
