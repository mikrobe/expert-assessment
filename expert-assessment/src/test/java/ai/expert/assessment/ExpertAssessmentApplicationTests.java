package ai.expert.assessment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import ai.expert.assessment.command.AddDocument;
import ai.expert.assessment.facade.Document;
import ai.expert.assessment.service.DocumentService;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = WebEnvironment.MOCK,
		classes = ExpertAssessmentApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class ExpertAssessmentApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private DocumentService documentService;
	
	@Test
	void testEverything() throws Exception {
		
		String name = "test.txt";
		String content = "test text";
		
		// Add a document to the database
		Document document = documentService.addDocument(new AddDocument(name, content.getBytes(), content.length(), "text/plain"));
		assertThat(document != null && document.getName().equals(name));
		 
		// Retrieve all documents from rest api
		mvc.perform(get("/api/v1/documents").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].name", is(name)));
	}
	
}
