package com.shivam.httpServer.core;

import com.shivam.httpServer.Exceptions.HttpParsingException;
import com.shivam.httpServer.Exceptions.HttpServerException;
import com.shivam.httpServer.core.io.WebRootHandler;
import com.shivam.httpServer.http.HttpMethod;
import com.shivam.httpServer.http.HttpParser;
import com.shivam.httpServer.http.HttpRequest;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class HttpConnectionHandler extends Thread{

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    final String CRLF = "\r\n";
    public HttpConnectionHandler(Socket socket) throws IOException {

        this.socket=socket;
        this.inputStream=socket.getInputStream();
        this.outputStream=socket.getOutputStream();
    }

    @Override
    public void run() {
        try {

//            InputStream is = socket.getInputStream();

//            OutputStream os = socket.getOutputStream();

            HttpParser httpParser=new HttpParser();

            HttpRequest httpRequest=httpParser.parseHttpRequest(inputStream);

            System.out.println("Http method found:"+httpRequest.getMethod());
            System.out.println("Http target found:"+httpRequest.getTarget());
            String html;
            if(!httpRequest.getMethod().equals(HttpMethod.POST))
                html = WebRootHandler.getInstance().getFileString(httpRequest.getTarget());
            else{
                StringBuilder bodyToReturn=new StringBuilder();
                bodyToReturn.append("Body found=>{");
                System.out.println("Body found=>{\n");
                HashMap<String,String> body=httpRequest.getBody();
                for(String key:body.keySet()){
                    System.out.println(key+":"+body.get(key));
                    bodyToReturn.append(key+":"+body.get(key)+"\n");
                }
                System.out.println("}");
                bodyToReturn.append("}\n");

                html=bodyToReturn.toString();
            }
            byte[] response=generateOKResponse(html);
            outputStream.write(response);

//            is.close();
//            os.close();
//            socket.close();

            System.out.println("Connection processing finished");
        }catch (Exception e) {
            System.out.println("Error in processing request:"+e.getMessage());
            try {
                outputStream.write(generateErrorResponse("Something went wrong"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new HttpServerException("Error in closing output stream:"+e.getMessage());
                }
            }
            if(inputStream!=null){
                try {
                    inputStream.close();
                }catch (IOException e){
                    throw new HttpServerException("Error in closing input stream:"+e.getMessage());
                }
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new HttpServerException("Error in closing socket:"+e.getMessage());
                }
            }
        }
    }

    private byte[] generateOKResponse(String body){
        String response =
                "HTTP/2.1 200 OK" + CRLF //HTTP_VERSION STATUS_CODE STATUS_MESSAGE
//                            + "Content-Length" + html.getBytes().length + CRLF //HEADE
                        + CRLF
                        + body //body
                        + CRLF + CRLF; //end
        return response.getBytes();
    }

    private byte[] generateErrorResponse(String body){
        String response =
                "HTTP/2.1 500 INTERNAL_SERVER_ERROR" + CRLF //HTTP_VERSION STATUS_CODE STATUS_MESSAGE
//                            + "Content-Length" + html.getBytes().length + CRLF //HEADE
                        + CRLF
                        + body //body
                        + CRLF + CRLF; //end
        return response.getBytes();
    }

}
