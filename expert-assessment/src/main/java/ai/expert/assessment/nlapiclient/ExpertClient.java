package ai.expert.assessment.nlapiclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.expert.assessment.utils.Globals;
import ai.expert.assessment.utils.PropertiesUtils;
import ai.expert.nlapi.security.Authentication;
import ai.expert.nlapi.security.Authenticator;
import ai.expert.nlapi.security.BasicAuthenticator;
import ai.expert.nlapi.security.Credential;
import ai.expert.nlapi.v2.API;
import ai.expert.nlapi.v2.cloud.Analyzer;
import ai.expert.nlapi.v2.cloud.AnalyzerConfig;
import ai.expert.nlapi.v2.cloud.Categorizer;
import ai.expert.nlapi.v2.cloud.CategorizerConfig;
import ai.expert.nlapi.v2.message.AnalyzeResponse;
import ai.expert.nlapi.v2.message.CategorizeResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Client to NLAPI 
 * Some refactoring from the one present in the githup assessment project
 * Unnecessary methods moved to the end of the file
 *
 */
@Component
@Slf4j
public class ExpertClient {
	
	@Autowired
	ExpertClientConfig config;
	
    public static enum ANALYSIS_TYPE { DISAMBIGUATION("disambiguation"), RELEVANTS("relevants"), ENTITIES("entities"), ANALYZE("analyze");
    	private String name;
    	ANALYSIS_TYPE(String name) {
    		this.name = name;
    	}
    	public String getName() {return name;};
    } ;
	
    private String strLogFileConf;
    private String strPropertiesFile;
	private Properties logFileConf;
    private Properties propertiesFile;

    private String expertAI_userToken = Globals.VOID_STRING;
    private String expertAI_Url = Globals.VOID_STRING;
    private String expertAI_AuthUrl = Globals.VOID_STRING;
    private String outputFolder = Globals.VOID_STRING;
    private String inputFolder = Globals.VOID_STRING;
    private String expertAI_user = Globals.VOID_STRING;
    private String expertAI_password = Globals.VOID_STRING;

    private final OkHttpClient httpClient;
	
	public ExpertClient() {
        this.httpClient = new OkHttpClient.Builder().readTimeout(Long.parseLong("5000"), TimeUnit.SECONDS).build();
    }
	
	@PostConstruct
    private boolean init() {
		if(config != null) {
			strLogFileConf = config.getLog4jconfig();
			strPropertiesFile = config.getApicallconfig();
			logFileConf = config.getLog4jConfigProperties();
			propertiesFile = config.getApicallConfigProperties();
		}
		
        if (propertiesFile != null) {
            log.info("\tBEGIN PROPERTIES FILE LOAD: " + strPropertiesFile);
            
            PropertiesUtils.crearProperties(propertiesFile);

            setExpertAI_Url(PropertiesUtils.getProperty("expertai.url"));
            log.info("\t\texpertai.url: " + getExpertAI_Url());

            setExpertAI_AuthUrl(PropertiesUtils.getProperty("expertai.auth.url"));
            log.info("\t\texpertai.auth.url: " + getExpertAI_AuthUrl());

            setInputFolder(PropertiesUtils.getProperty("expert.inputFolder"));
            log.info("\t\tinputFolder: " + getInputFolder());

            setOutputFolder(PropertiesUtils.getProperty("expert.outputFolder"));
            log.info("\t\toutputFolder: " + getOutputFolder());

            setExpertAI_user(PropertiesUtils.getProperty("expertai.username"));
            log.info("\t\texpertAI_user: " + getExpertAI_user());

            setExpertAI_password(PropertiesUtils.getProperty("expertai.password"));
            log.info("\t\texpertAI_password: " + getExpertAI_password());

            log.info("\tEND PROPERTIES FILE LOAD");
            log.info("\t***************");
            log.info("\tINIT STATUS: OK");
            log.info("\t***************");
            return Boolean.TRUE;

        } else {
            log.info("\tProperties file not found: " + strPropertiesFile);
            log.info("\t***************");
            log.info("\tINIT STATUS: KO");
            log.info("\t***************");
            return Boolean.FALSE;
        }
    }


    public boolean getToken() {
        boolean tokenOk = Boolean.FALSE;
        Response response;
        RequestBody body;

        JSONObject json = new JSONObject();
        json.put("username", getExpertAI_user());
        json.put("password", getExpertAI_password());

        body = RequestBody.create(Globals.CONTENT, json.toString());

        Request request = new Request.Builder().url(getExpertAI_AuthUrl()).post(body).build();

        try {
            log.info("\t\tCalling WS to retrieve the TOKEN");
            log.info("\t\tWS-host: " + getExpertAI_AuthUrl());
            response = httpClient.newCall(request).execute();
            log.info("\t\tCall executed");
            if (!response.isSuccessful()) {
                log.error("\t\tError calling WS for retrieve the TOKEN");
                log.info("\t\tTOKEN NOT RETRIEVED");
            } else {
                setExpertAI_userToken(response.body().string());
                response.close();
                log.info("\t\tTOKEN RETRIEVED");
                tokenOk = Boolean.TRUE;
            }
        } catch (IOException ex) {
            log.error("\t\tError calling WS for retrieve the TOKEN");
            log.error(ex.getMessage());
            log.info("\t\tTOKEN NOT RETRIEVED");
        }

        return tokenOk;
    }

    //Method for setting the authentication credentials - set your credentials here.
    public Authentication createAuthentication() throws Exception {
        Authenticator authenticator = new BasicAuthenticator(new Credential(getExpertAI_user(), getExpertAI_password(), getExpertAI_userToken()));
        return new Authentication(authenticator);
    }

    //Method for selecting the resource to be call by the API; as today, the API provides the IPTC classifier only, and
    //five languages such as English, French, Spanish, German and Italian
    public Categorizer createCategorizer() throws Exception {
        return new Categorizer(CategorizerConfig.builder()
                .withVersion(API.Versions.V2)
                .withTaxonomy(API.Taxonomies.IPTC.value())
                .withLanguage(API.Languages.en)
                .withAuthentication(createAuthentication())
                .build());
    }

    //Method for selecting the resource to be call by the API; as today, the API provides the standard context only, and
    //five languages such as English, French, Spanish, German and Italian
    public Analyzer createAnalyzer() throws Exception {
        return new Analyzer(AnalyzerConfig.builder()
                .withVersion(API.Versions.V2)
                .withContext(API.Contexts.STANDARD.value())
                .withLanguage(API.Languages.en)
                .withAuthentication(createAuthentication())
                .build());
    }

    private String getTextToProcess(File file) throws IOException {
        String jsonTxt = Globals.VOID_STRING;
        JSONObject jsonObj;
        if (file.getName().contains(".json")) {

            jsonTxt = new String(Files.readAllBytes(Paths.get(file.getPath())));
            jsonObj = new JSONObject(jsonTxt);
            jsonObj = jsonObj.getJSONObject("document");
            jsonTxt = jsonObj.getString("text");
        } else {
        	jsonTxt = new String(Files.readAllBytes(Paths.get(file.getPath())));

            if(jsonTxt.length() > 10000){
                jsonTxt = jsonTxt.substring(0,10000);
            }
        }
        return jsonTxt;
    }

	public CategorizeResponse categorize(File file, Categorizer categorizer) throws Exception {
    	CategorizeResponse categorization = null;
    	String txt = getTextToProcess(file);

        //Perform the IPTC classification and store it into a Response Object
        categorization = categorizer.categorize(txt);

        return categorization;
	}

    public AnalyzeResponse analyze(File file, Analyzer analyzer, ANALYSIS_TYPE type) throws Exception {
    	AnalyzeResponse extraction = null;
        String txt = getTextToProcess(file);
        
        switch(type) {
        case DISAMBIGUATION:
        	// Disambiguation Analisys
        	 extraction = analyzer.disambiguation(txt);
        	 break;
        case RELEVANTS:
        	// Relevants Analisys
        	extraction = analyzer.relevants(txt);
        	break;
        case ENTITIES:
        	// Entities Analisys
        	extraction = analyzer.entities(txt);
        	break;
        case ANALYZE:
        	// Full Analisys
        	extraction = analyzer.analyze(txt);
        }
		return extraction;
	}
    

	/**
     * @return the expertAI_token
     */
    public String getExpertAI_userToken() {
        return expertAI_userToken;
    }

    /**
     * @param expertAI_userToken the expertAI_token to set
     */
    public void setExpertAI_userToken(String expertAI_userToken) {
        this.expertAI_userToken = expertAI_userToken;
    }

    /**
     * @return the expertAI_Url
     */
    public String getExpertAI_Url() {
        return expertAI_Url;
    }

    /**
     * @param expertAI_Url the expertAI_Url to set
     */
    public void setExpertAI_Url(String expertAI_Url) {
        this.expertAI_Url = expertAI_Url;
    }

    /**
     * @return the outputFolder
     */
    public String getOutputFolder() {
        return outputFolder;
    }

    /**
     * @param outputFolder the outputFolder to set
     */
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * @return the strLogFileConf
     */
    public String getStrLogFileConf() {
        return strLogFileConf;
    }

    /**
     * @param strLogFileConf the strLogFileConf to set
     */
    public void setStrLogFileConf(String strLogFileConf) {
        this.strLogFileConf = strLogFileConf;
    }

    /**
     * @return the strPropertiesFile
     */
    public String getStrPropertiesFile() {
        return strPropertiesFile;
    }

    /**
     * @param strPropertiesFile the strPropertiesFile to set
     */
    public void setStrPropertiesFile(String strPropertiesFile) {
        this.strPropertiesFile = strPropertiesFile;
    }

    /**
     * @return the inputFolder
     */
    public String getInputFolder() {
        return inputFolder;
    }

    /**
     * @param inputFolder the inputFolder to set
     */
    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }

    /**
     * @return the user
     */
    public String getExpertAI_user() {
        return expertAI_user;
    }

    /**
     * @param user the user to set
     */
    public void setExpertAI_user(String user) {
        this.expertAI_user = user;
    }

    /**
     * @return the password
     */
    public String getExpertAI_password() {
        return expertAI_password;
    }

    /**
     * @param password the password to set
     */
    public void setExpertAI_password(String password) {
        this.expertAI_password = password;
    }

    /**
     * @return the expertAI__AuthUrl
     */
    public String getExpertAI_AuthUrl() {
        return expertAI_AuthUrl;
    }

    /**
     * @param expertAI_AuthUrl the expertAI__AuthUrl to set
     */
    public void setExpertAI_AuthUrl(String expertAI_AuthUrl) {
        this.expertAI_AuthUrl = expertAI_AuthUrl;
    }

//  public void startWithWrapper() {
//
//      log.info("");
//      log.info("\t-- 2.1 --");
//      log.info("\tBEGIN FILE READING PROCESS");
//      FileReader reader = new FileReader();
//      reader.readFiles(getInputFolder());
//      log.info("\tEND FILE READING PROCESS");
//      log.info("\tTOTAL PROCESSING FILES: " + reader.getFiles().size());
//
//      try {
//
//          Categorizer categorizer = createCategorizer();
//
//          log.info("");
//          log.info("\t-- 2.2 --");
//          log.info("\tBEGIN FILE ANALYSIS (CATEGORIZATION)");
//          for (File file : reader.getFiles()) {
//              try {
//              	CategorizeResponse categorization = categorize(file, categorizer);
//
//                  log.info("\t\tSaving categorization: " + file.getName() + Globals.ANALYSIS_TYPE_CAT + Globals.OUTPUT_EXTENSION);
//                  saveAnalyzedFile(new JSONObject(categorization.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_CAT + Globals.OUTPUT_EXTENSION);
//
//              } catch (IOException ex) {
//                  log.error(ex.getMessage());
//              } catch (NullPointerException exNull){
//                  log.error(exNull.getMessage());
//              }
//          }
//          log.info("\tEND FILE ANALYSIS (CATEGORIZATION)");
//
//          try {
//
//              //Perform the IPTC classification and store it into a Response Object
//              Analyzer analyzer = createAnalyzer();
//
//              log.info("");
//              log.info("\t-- 2.3 --");
//              log.info("\tBEGIN FILE ANALYSIS (EXTRACTION)");
//              for (File file : reader.getFiles()) {
//
//                  try {
//
//                  	AnalyzeResponse extraction = analyze(file, analyzer);
//                  	
//                      log.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_DIS + Globals.OUTPUT_EXTENSION);
//                      saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_DIS + Globals.OUTPUT_EXTENSION);
//                      log.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_REL + Globals.OUTPUT_EXTENSION);
//                      saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_REL + Globals.OUTPUT_EXTENSION);
//                      log.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_ENT + Globals.OUTPUT_EXTENSION);
//                      saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_ENT + Globals.OUTPUT_EXTENSION);
//                      log.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_FULL + Globals.OUTPUT_EXTENSION);
//                      saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_FULL + Globals.OUTPUT_EXTENSION);
//
//                  } catch (IOException ex) {
//                      log.error(ex.getMessage());
//                  } catch (NullPointerException exNull){
//                      log.error(exNull.getMessage());
//                  }
//              }
//              log.info("\tEND FILE ANALYSIS (EXTRACTION)");
//
//
//          } catch (Exception ex) {
//              ex.printStackTrace();
//          }
//      } catch (Exception ex) {
//          ex.printStackTrace();
//      }
//  }
//  

	/**
	 * FOR BACKWARD compatibility I leave the original code unchanged.
	 * @param file
	 * @param analyzer
	 * @return
	 * @throws Exception
	 */
//    public AnalyzeResponse analyze(File file, Analyzer analyzer) throws Exception {
//    	AnalyzeResponse extraction = null;
//        String txt = getTextToProcess(file);
//        
//        // Disambiguation Analisys
//        extraction = analyzer.disambiguation(txt);
//        // Relevants Analisys
//        extraction = analyzer.relevants(txt);
//        // Entities Analisys
//        extraction = analyzer.entities(txt);
//        // Full Analisys
//        extraction = analyzer.analyze(txt);
//
//		return extraction;
//	}
    
//  public boolean saveAnalyzedFile(JSONObject outputFile, String fileName) {
//  boolean savedOk = Boolean.FALSE;
//  try {
//      Files.write(Paths.get(getOutputFolder() + fileName), Collections.singleton(outputFile.toString(3)));
//      savedOk = Boolean.TRUE;
//  } catch (JsonProcessingException e) {
//      log.error(e.getMessage());
//  } catch (IOException ex) {
//      log.error(ex.getMessage());
//  }
//
//  return savedOk;
//}
}
