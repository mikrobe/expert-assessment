package ai.expert.assessment.ui.viewmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import ai.expert.assessment.facade.Document;
import ai.expert.assessment.ui.FrontendService;


@VariableResolver(DelegatingVariableResolver.class)
public class DocumentsVM {

	@WireVariable
	private FrontendService frontendService;

	private Map<String, Media> dataMedias = new HashMap<>();
	
	@Init
	public void init() {
	}

	public Map<String, Media> getDataMedias() {
		return dataMedias;
	}

	public void setDataMedias(Map<String, Media> dataMedias) {
		this.dataMedias = dataMedias;
	}
	
	public List<String> getDataNames() {
		return dataMedias.keySet().stream().collect(Collectors.toList());
	}

	public boolean isValid() {
		return true;
	}
	
	public List<Document> getDocuments() {
		return frontendService.getDocuments();
	}
	
	 @Command
	 @NotifyChange("documents")
     public void doUpload(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) {
         UploadEvent upEvent = null;
         Object objUploadEvent = ctx.getTriggerEvent();
         if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
             upEvent = (UploadEvent) objUploadEvent;
         }
         if (upEvent != null) {
        	 Media[] medias = upEvent.getMedias();   
        	 addToRepository(medias);
         }
     }
	 
	private void addToRepository(Media[] medias) {
   	 if(medias != null) {
   		 List<Document> newDocuments = frontendService.saveDocuments(medias);
	 }
	}

	@Command
	@NotifyChange("documents")
	public void delete(@BindingParam("doc") Document document) {
		frontendService.deleteDocument(document);
	}
	
	@Command
	public void gotoDocument(@BindingParam("doc") Document document) {
		Sessions.getCurrent().setAttribute("doc", document);
		Map<String, Object> args = new HashMap<>();
		args.put("id", document.getUid());
		Executions.getCurrent().pushArg(args);
		Executions.getCurrent().setAttribute("id", document.getUid());
		Executions.getCurrent().sendRedirect("/document?id="+ document.getUid(),true);
	}
}