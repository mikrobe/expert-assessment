package ai.expert.assessment.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import ai.expert.assessment.facade.DocumentCategory;
import ai.expert.assessment.model.DocumentCategoryEntity;
import ai.expert.nlapi.v2.model.Category;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to convert categories between the category received from NLAPI client, facade (DTO) and entity
 */
@Component
@Slf4j
@AllArgsConstructor
public class CategoryMapper {
	
	private final ModelMapper modelMapper;
	
	/**
	 * 
	 * @param nlapiModelCategory
	 * @return
	 */
	public DocumentCategoryEntity nlapiModelToEntity(Category nlapiModelCategory) {
		log.debug("Convert 'NL api Category' to new category entity instance, ['filename': {}]",
				nlapiModelCategory.getId());
		
		DocumentCategoryEntity entity = modelMapper.map(nlapiModelCategory, DocumentCategoryEntity.class);
		
		log.debug("Category entity with id {} initialized.", entity.getId());
		return entity;
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public DocumentCategory entityToDto(DocumentCategoryEntity entity) {
		DocumentCategory dto = null;
		if(entity != null) {
			log.debug("Convert 'DocumentCategory' entity to DTO. ['id']", entity.getId());
			dto = modelMapper.map(entity, DocumentCategory.class);
			if(dto != null) {
				log.debug("DTO initialized with id {}", dto.getId());
			}
		}
		return dto;
	}
	
	/**
	 * 
	 * @param nlapiModelCategoryList
	 * @return
	 */
	public List<DocumentCategoryEntity> nlapiModelToEntityList(List<Category> nlapiModelCategoryList) {
		return nlapiModelCategoryList.stream()
						.map(c -> modelMapper.map(c, DocumentCategoryEntity.class))
				  		.collect(Collectors.toList());
	}

	/**
	 * 
	 * @param categoryEntities
	 * @return
	 */
	public List<DocumentCategory> entityToDtoList(List<DocumentCategoryEntity> categoryEntities) {
		return categoryEntities.stream()
				.map(c -> modelMapper.map(c, DocumentCategory.class))
		  		.collect(Collectors.toList());
	}
}
