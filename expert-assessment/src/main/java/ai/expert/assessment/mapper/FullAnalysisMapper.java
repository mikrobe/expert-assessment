package ai.expert.assessment.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.expert.assessment.facade.FullAnalysis;
import ai.expert.assessment.model.FullAnalysisEntity;
import ai.expert.nlapi.v2.message.AnalyzeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to convert fullanalysis results between facade (DTO) and entity
 */
@Component
@Slf4j
@AllArgsConstructor
public class FullAnalysisMapper {
	
	private final ModelMapper modelMapper;
	
	/**
	 * 
	 * @param dto
	 * @return
	 */
	public FullAnalysisEntity dtoToEntity(FullAnalysis dto) {
		FullAnalysisEntity entity = modelMapper.map(dto, FullAnalysisEntity.class);
		entity.setResponse(dto.getAnalyzeResponse().toJSON());
		return entity;
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public FullAnalysis entityToDto(FullAnalysisEntity entity) {
		FullAnalysis dto = null;
		if(entity != null) {
			dto = modelMapper.map(entity, FullAnalysis.class);
			if(dto != null) {
				ObjectMapper om = new ObjectMapper();
				try {
					if(entity.getResponse() != null) {
						AnalyzeResponse analyzeResponse = om.readValue(entity.getResponse(), AnalyzeResponse.class);
						dto.setAnalyzeResponse(analyzeResponse);
					}
				} catch (JsonProcessingException e) {
					log.error("AnalyzeResponse stored in FullAnalysis with id {} has errors {}", entity.getUid(), e.getMessage());
					log.error("Analyzeresponse content:\n{}", entity.getResponse());
				}
				
				log.debug("DTO '{}' initialized with id {}", dto.getUid());
			}
		}
		return dto;
	}

	/**
	 * 
	 * @param entityList
	 * @return
	 */
	public List<FullAnalysis> entityToDtoList(List<FullAnalysisEntity> entityList) {
		return entityList.stream()
				.map(c -> entityToDto(c))
		  		.collect(Collectors.toList());
	}
	
}
