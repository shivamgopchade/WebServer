package com.shivam.httpServer.config;

import com.shivam.httpServer.Exceptions.HttpServerException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

     private static ConfigurationManager myConfigurationManager;
     private static Configuration configuration;
     private ConfigurationManager(){

     }

     public static ConfigurationManager getInstance(){
         if(myConfigurationManager==null)
             myConfigurationManager=new ConfigurationManager();
         return myConfigurationManager;
     }

     //To load configuration file
     public void loadConfigurationFile(String filePath) {

            try(InputStream fis=new FileInputStream(filePath)){
                Properties properties=new Properties();
                properties.load(fis);
                configuration=new Configuration(Integer.parseInt(properties.getProperty("port")),properties.getProperty("webroot"));

            } catch (IOException e) {
                throw new HttpServerException("Error in loading configuration file");
            }
     }

     //To get configuration
     public Configuration getCurrentConfiguration(){
         return configuration;
     }
}
