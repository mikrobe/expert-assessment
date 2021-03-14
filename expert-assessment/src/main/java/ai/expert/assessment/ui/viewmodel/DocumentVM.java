package ai.expert.assessment.ui.viewmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.ListModelList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.expert.assessment.facade.Categorization;
import ai.expert.assessment.facade.Document;
import ai.expert.assessment.facade.DocumentCategory;
import ai.expert.assessment.facade.FullAnalysis;
import ai.expert.assessment.service.AnalyzeSuccess;
import ai.expert.assessment.ui.FrontendService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@VariableResolver(DelegatingVariableResolver.class)
@Slf4j
public class DocumentVM {

	@WireVariable
	private FrontendService frontendService;
	
	Document document;
	Categorization categorization;
	String categorizationJson;
	private List<FullAnalysis> analysisList;
	Map<String, String> analysisJsonMap = new HashMap<>();
	List<AnalyzeSuccess> analysisResult;

	private String msgError = null;

	@Init
	public void init() {
		Long documentUid = null;
		try {
			String paramValue = Executions.getCurrent().getParameter("id");
			documentUid = Long.parseLong(paramValue);
			this.document = frontendService.findDocument(documentUid);
		} catch (Exception e) {
			this.msgError = "Document not found";
		}

		if(this.document == null) {
			this.document = (Document) Sessions.getCurrent().getAttribute("doc");
			Sessions.getCurrent().removeAttribute("doc");
		}
		
		if(this.document != null) {
			initCategorization(document.getUid());
			initAnalysis(document.getUid());
		}
	}
	
	private void initCategorization(@NonNull Long documentUid) {
		categorization = frontendService.getCategorization(document.getUid());
		categorizationJson = (categorization != null) ? Optional.ofNullable(this.categorization.getCategorizeResponse()).orElse(null).toJSON() : null;
	}
	
	private void initAnalysis(@NonNull Long uid) {
		analysisList = frontendService.getFullAnalysis(document.getUid());
        ObjectMapper om = new ObjectMapper();
		analysisList.stream()
					.forEach(a -> {
						try {
							analysisJsonMap.put(a.getType(), om.writerWithDefaultPrettyPrinter()
													.writeValueAsString(a.getAnalyzeResponse().getData()));
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
	}
	
	public Document getDocument() {
		return document;
	}
	
	public String getDocumentContent() {
		return new String(frontendService.getDocumentContent(document.getUid()));
	}
	
	public Categorization getCategorization() {
		return categorization;
	}
	
	public List<DocumentCategory> getCategories() {
		return categorization != null ? new ListModelList<>(categorization.getCategories()) : null;
	}
	
	public String getCategorizationJson() {
		return categorizationJson;
	}
	
	public String getFormattedAnalysisJson(String type) {
		return (analysisJsonMap != null) ? getHtmlData(analysisJsonMap.get(type)) : null;
	}
	
	public Map<String, String> getAnalysisJsonMap() {
		return analysisJsonMap;
	}
	
	public List<AnalyzeSuccess> getAnalysisResult() {
		return analysisResult;
	}
	
	public String getMessageError() {
		return msgError;
	}
	
	public String getTitle() {
		return document != null ? document.getName() : "Document not found";
	}
	
	@Command
	@NotifyChange({"categorizationJson", "categorization", "categories", "document"})
	public void categorize() {
		try {
			boolean result = frontendService.categorize(document);
			initCategorization(document.getUid());
		} catch (Exception e) {
			log.error("Categorizing document {}: {}", document.getName(), e.getMessage());
		}
	}
	
	@Command
	@NotifyChange({"analysisResult", "analysisJsonList"})
	public void analyze() {
		try {
			this.analysisResult = frontendService.analyze(document);
			initAnalysis(document.getUid());
		} catch (Exception e) {
			log.error("Analysing document {}: {}", document.getName(), e.getMessage());
		}
	}
	
	@Command
	public void gotoDocuments() {
		Executions.sendRedirect("/documents");
	}
	
	public String getFormattedCategorizationJson() {
		return getHtmlData(categorizationJson);
	}
	
	/**
	 * Get the JSON data formated in HTML
	 */ 
	public String getHtmlData( String strJsonData ) {
	    return strJsonData != null ? jsonToHtml(new JSONObject(strJsonData)) : null;
	}

	/**
	 * convert json Data to structured Html text
	 * 
	 * @param json
	 * @return string
	 */
	private String jsonToHtml( Object obj ) {
	    StringBuilder html = new StringBuilder( );

	    try {
	        if (obj instanceof JSONObject) {
	            JSONObject jsonObject = (JSONObject)obj;
	            String[] keys = JSONObject.getNames( jsonObject );

	            html.append("<div class=\"json_object\">");

	            if (keys.length > 0) {
	                for (String key : keys) {
	                    // print the key and open a DIV
	                    html.append("<div><span class=\"json_key\">")
	                        .append(key).append("</span> : ");

	                    Object val = jsonObject.get(key);
	                    // recursive call
	                    html.append( jsonToHtml( val ) );
	                    // close the div
	                    html.append("</div>");
	                }
	            }

	            html.append("</div>");

	        } else if (obj instanceof JSONArray) {
	            JSONArray array = (JSONArray)obj;
	            for ( int i=0; i < array.length( ); i++) {
	                // recursive call
	                html.append( jsonToHtml( array.get(i) ) );                    
	            }
	        } else {
	            // print the value
	            html.append( obj );
	        }                
	    } catch (JSONException e) { return e.getLocalizedMessage( ) ; }

	    return html.toString( );
	}
	
}
