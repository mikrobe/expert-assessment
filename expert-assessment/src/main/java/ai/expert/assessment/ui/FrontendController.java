package ai.expert.assessment.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The ZK frontend controller
 * 
 * @author wsandri
 *
 */
@Controller
public class FrontendController {

	
//	@GetMapping("/")
//	public String login() {
//		return "documents";
//	}
	
	@GetMapping("/documents")
	public String documents() {
		return "documents";
	}
	
	@GetMapping("/document")
	public String document() {
		return "document";
	}
}
