package com.shivam.httpServer.config;


public class Configuration {
    private int port;
    private String webroot;

    public Configuration(){

    }

    public Configuration(int port, String webroot) {
        this.port = port;
        this.webroot = webroot;
    }

    public void setPort(int port){
        this.port=port;
    }

    public int getPort(){
        return this.port;
    }

    public void setWebroot(String webroot){
        this.webroot=webroot;
    }

    public String getWebroot(){
        return this.webroot;
    }
}
