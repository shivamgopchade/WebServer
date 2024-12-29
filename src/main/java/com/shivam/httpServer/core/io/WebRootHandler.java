package com.shivam.httpServer.core.io;

import com.shivam.httpServer.Exceptions.HttpServerException;
import com.shivam.httpServer.config.ConfigurationManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class WebRootHandler {

    private File webroot;

    private static WebRootHandler webRootHandler=null;

    private WebRootHandler(String path){
        this.webroot=new File(path);
        if(!webroot.exists() || !webroot.isDirectory())
            throw new HttpServerException("Web root does not exist");
    }

    public static WebRootHandler getInstance(){
        if(webRootHandler==null){
            webRootHandler=new WebRootHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getWebroot());
        }

        return webRootHandler;
    }

    private boolean CheckIfEndsWithSlash(String relativePath){
        if(relativePath.endsWith("/"))
            return true;
        return  false;
    }

    /*
    *
    *  Check if file path exist in relative to webroot
    *
    */
    private boolean CheckIfRelativePathExist(String relativePath) throws IOException {
        File file=new File(webroot,relativePath);

        if(!file.exists())
            return false;

        else return file.getCanonicalPath().startsWith(webroot.getCanonicalPath());

    }

    public String getMimeType(String relativePath) throws IOException {
        if(CheckIfEndsWithSlash(relativePath)){
            relativePath+="index.html"; //default file
        }

        if(!CheckIfRelativePathExist(relativePath)){
            throw new HttpServerException("File not found for "+relativePath);
        }

        File file=new File(webroot,relativePath);
        String mimeType= URLConnection.getFileNameMap().getContentTypeFor(file.getName());

        if(mimeType==null)
            return "application/octet-stream"; //default mime type

        return mimeType;
    }

    public byte[] getFileByteArray(String relativePath) throws IOException {
        if(CheckIfEndsWithSlash(relativePath)){
            relativePath+="index.html"; //default file
        }

        if(!CheckIfRelativePathExist(relativePath)){
            throw new HttpServerException("File not found for "+relativePath);
        }

        File file=new File(webroot,relativePath);

        try(FileInputStream fis=new FileInputStream(file)){
            return fis.readAllBytes();
        }catch (IOException e){
            throw new HttpServerException("Error in reading bytes of index.html");
        }

    }

    public String getFileString(String fileRelativePath) throws FileNotFoundException {
        if(fileRelativePath==null || fileRelativePath.isEmpty() || fileRelativePath.equals("/"))
            return readHtmlPage("index.html");

        File htmlFile=new File(this.webroot.getPath(),fileRelativePath);
        if(htmlFile.exists())
            return readHtmlPage(fileRelativePath);
        else return readHtmlPage("404_Page.html");
    }

    private String readHtmlPage(File htmlFile) throws FileNotFoundException {

        if(htmlFile.exists()){
            try(FileInputStream fis=new FileInputStream(htmlFile)){
                int _byte;
                StringBuilder fileContent=new StringBuilder();
                while((_byte=fis.read())!=-1){
                    fileContent.append((char)_byte);
                }
                return fileContent.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new FileNotFoundException();
        }

    }

    private String readHtmlPage(String fileRelativePath) throws FileNotFoundException {
        File htmlFile=new File(this.webroot.getPath(),fileRelativePath);
        if(htmlFile.exists()){
            try(FileInputStream fis=new FileInputStream(htmlFile)){
                int _byte;
                StringBuilder fileContent=new StringBuilder();
                while((_byte=fis.read())!=-1){
                    fileContent.append((char)_byte);
                }
                return fileContent.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new FileNotFoundException();
        }

    }
}
