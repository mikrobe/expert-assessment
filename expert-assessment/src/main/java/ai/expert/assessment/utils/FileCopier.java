package ai.expert.assessment.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author oserret
 */
@Slf4j
public class FileCopier {

    private static final Logger FILE_COPIER_LOGGER = log;

    public FileCopier() {

    }

    public void copyFiles(String outputFolder, ArrayList<File> files, String customdir_param, Boolean run_param, Integer max_nb_cluster_param, String customid_param, Integer sleepTime_param) {

        for (File file : files) {
            if (file.isFile()) {
                try {
                    FILE_COPIER_LOGGER.info("\t\t\tCopying file: " + file.getName() + " to: " + outputFolder);
                    Files.copy(file.toPath(), (new File(outputFolder + file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    writeJsonFile(file.getName(), outputFolder, customdir_param, run_param, max_nb_cluster_param, customid_param);
                    Files.delete(file.toPath());
                    FILE_COPIER_LOGGER.info("\t\t\tSleeping thread for : " + sleepTime_param + " miliseconds");
                    Thread.sleep(sleepTime_param);
                } catch (IOException|InterruptedException ex) {
                    FILE_COPIER_LOGGER.error("Error copying file: " + file.getName() + " to: " + outputFolder);
                    FILE_COPIER_LOGGER.error(ex.getMessage());
                }
            }
        }
    }

    public void writeJsonFile(String jsonFile, String outputFolder, String customdir_param, Boolean run_param, Integer max_nb_cluster_param, String customid_param) throws IOException {

        JSONObject outputFile = new JSONObject();
        JSONObject customid_customdir = new JSONObject();
        JSONObject run_max_nb_cluster = new JSONObject();
        
        customid_customdir.put("customid",customid_param);
        customid_customdir.put("customdir",customdir_param);
                
        run_max_nb_cluster.put("run", run_param);
        run_max_nb_cluster.put("max_nb_cluster", max_nb_cluster_param);
       
        outputFile.put("asr",customid_customdir);
        outputFile.put("diar",run_max_nb_cluster);
        
        String outputStream = outputFile.toJSONString().replace("\\","");
        
        String jsonFileName = jsonFile.replace(".wav","");
        
        Files.write(Paths.get(outputFolder+jsonFileName+".opt"), outputStream.getBytes());
    }

}