package ai.expert.assessment.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.expert.assessment.facade.Categorization;
import ai.expert.assessment.facade.DocumentCategory;
import ai.expert.assessment.model.CategorizationEntity;
import ai.expert.nlapi.v2.message.CategorizeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to convert categorizations between facade (DTO) and entity
 */
@Component
@Slf4j
@AllArgsConstructor
public class CategorizationMapper {
	
	private final ModelMapper modelMapper;
	
	/**
	 * 
	 * @param entity The categorization entity to convert
	 * @param categories Category list associated to the categorization
	 * @return Categorization dto with embedded categories
	 */
	public Categorization entityToDto(CategorizationEntity entity, List<DocumentCategory> categories) {
		Categorization dto = null;
		if(entity != null) {
			dto = modelMapper.map(entity, Categorization.class);
			if(dto != null) {
				dto.setCategories(categories);
				ObjectMapper om = new ObjectMapper();
				try {
					if(entity.getResponse() != null) {
						CategorizeResponse categorizeResponse = om.readValue(entity.getResponse(), CategorizeResponse.class);
						dto.setCategorizeResponse(categorizeResponse);
					}
				} catch (JsonProcessingException e) {
					log.error("CategorizeResponse stored in Categorization with id {} has errors {}", entity.getUid(), e.getMessage());
					log.error("CategorizeResponse content:\n{}", entity.getResponse());
				}
				log.debug("DTO '{}' initialized with id {}", dto.getUid());
			}
		}
		return dto;
	}
}
