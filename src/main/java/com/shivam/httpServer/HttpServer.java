package com.shivam.httpServer;


import com.shivam.httpServer.config.Configuration;
import com.shivam.httpServer.config.ConfigurationManager;
import com.shivam.httpServer.core.HttpServerListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
*
* HttpServer driver class
*
* */
public class HttpServer {

    public static void main(String[] args) throws IOException {
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/application.properties");
        Configuration myconfig=ConfigurationManager.getInstance().getCurrentConfiguration();

        HttpServerListener httpServerListener=new HttpServerListener(myconfig.getPort());
        httpServerListener.start();

        System.out.println("Server running on thread so this runs parallel");

    }
}