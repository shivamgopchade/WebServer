package com.shivam.httpServer.core;

import com.shivam.httpServer.Exceptions.HttpServerException;
import com.shivam.httpServer.core.io.WebRootHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServerListener extends Thread{

    private int port;
    private final ServerSocket serverSocket;

    public HttpServerListener(int port) throws IOException {
            this.port=port;
            this.serverSocket=new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            while(serverSocket!=null && serverSocket.isBound() && !serverSocket.isClosed()){
                Socket socket=serverSocket.accept();
                HttpConnectionHandler httpConnectionHandler=new HttpConnectionHandler(socket);
                try{
                    httpConnectionHandler.start();
                }catch (HttpServerException e){
                    System.out.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new HttpServerException("error in HttpServerListener:"+e.getMessage());
        }
        finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new HttpServerException("Error in closing server socket"+e.getMessage());
                }
            }
        }

    }
}
