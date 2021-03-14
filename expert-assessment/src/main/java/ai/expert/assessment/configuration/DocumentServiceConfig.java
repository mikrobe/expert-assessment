package ai.expert.assessment.configuration;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@ConfigurationProperties(prefix = "documentservice")
@Data
@Slf4j
public class DocumentServiceConfig {

	private String documentFolder;
	
	@PostConstruct
    private void postConstruct() {
		log.info("Document folder: {}", documentFolder);
    }
}
