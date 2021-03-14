package ai.expert.assessment.utils;

import java.io.File;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author oserret
 */
@Slf4j
public class FileReader {

    private ArrayList<File> files = new ArrayList<>();

    public FileReader() {
       
    }

    public void readFiles(String inputFolder) {

        File folder = new File(inputFolder);
        File[] listOfFiles = null;

        listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                log.info("\t\tReading file: " + file.getName());
                getFiles().add(file);
            } else {
                log.info("\t\tBegin directory reading: " + file.getPath());
                readFiles(file.getPath());
            }
        }
    }

    /**
     * @return the files
     */
    public ArrayList<File> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

}