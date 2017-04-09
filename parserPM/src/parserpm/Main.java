/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parserpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 *
 * @author sbt-steshenko-ma
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        String configPath = args[0].toString().replace("\\", "/");
        
      //  System.out.println(configPath);

        

        new JFXPanel();
        Properties timePath = getPropertiesTimePath(configPath);
        String start = timePath.getProperty("start");
        String end = timePath.getProperty("end");
        String path = timePath.getProperty("path");

        deleteDirectory(path);

        Properties urls = getPropertiesUrls(configPath);
        if(urls == null){
            System.err.println("Не могу найти файл с конфигурацией.");
            Platform.exit();
            return;
        }

        for (Map.Entry<Object, Object> entry : urls.entrySet()) {

            String graphName = entry.getKey().toString();
            String url = entry.getValue().toString();

            Reader reader = new Reader(start, end, url, path, graphName);

        }
        Platform.exit();
    }

    public static Properties getPropertiesTimePath(String configPath) {

        Properties config = new Properties();
        try {
            config.load(new FileInputStream(configPath+"/timePath.properties"));
        } catch (IOException e) {
            System.err.println(e);
        }

        return config;
    }

    public static Properties getPropertiesUrls(String configPath) {

        Properties config = new Properties();
        try {
            config.load(new FileInputStream(configPath+"/urls.properties"));
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
        return config;

    }

    public static void deleteDirectory(String path) {

        File directory = new File(path);

        if (directory.exists()) {

            File[] contents = directory.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    f.delete();
                }
            }
            directory.delete();

            label:
            while (!directory.exists()) {
                directory.mkdir();
                continue label;
            }

        }

        label_2:
        while (!directory.exists()) {
                directory.mkdir();
                continue label_2;
            }

    }

}
