package ai.expert.assessment.nlapiclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@ConfigurationProperties(prefix = "expertclient")
@Data
@Slf4j
public class ExpertClientConfig {
	
	private String log4jconfig;
	private String apicallconfig;
	
	private Resource log4jConfigResource;
	private Resource apicallConfigResource;
	
	private Properties log4jConfigProperties = new Properties();
	private Properties apicallConfigProperties = new Properties();
	
	@PostConstruct
    private void postConstruct() throws IOException {
		log.info("Log4j configuration file: {}", log4jconfig);
		log.info("Api call configuration file: {}", apicallconfig);
		loadLog4jConfig();
		loadApicallConfig();
    }
	
	public void loadLog4jConfig() throws IOException {
		if(log4jconfig != null) {
//			log4jConfigResource = new ClassPathResource(log4jconfig);
//			log4jConfigProperties.load(log4jConfigResource.getInputStream());
			log4jConfigProperties = loadProperties(log4jconfig);
		}
	}
	
	public void loadApicallConfig() throws IOException {
		if(apicallconfig != null) {
//			apicallConfigResource = new ClassPathResource(apicallconfig);
//			apicallConfigProperties.load(apicallConfigResource.getInputStream());
			apicallConfigProperties = loadProperties(apicallconfig);
		}
	}
	
	private Properties loadProperties(String filepath) {
		Properties properties = null;

		try {
			// first from the filesystem
			Properties p = new Properties();
			p.load(new FileInputStream(filepath));
			properties = p;
		} catch (Exception e) {
			try {
				// else from the classpath
				Resource r= new ClassPathResource(filepath);
				Properties p = new Properties();
				properties = p;
				p.load(r.getInputStream());
			} catch (Exception e1) {
			}
		}
		if (properties == null) { 
			log.error("Could not load properties file [{}]", filepath);
		}
		return properties;
	}
}
