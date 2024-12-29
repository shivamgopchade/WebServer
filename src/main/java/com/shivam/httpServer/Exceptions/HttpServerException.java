package com.shivam.httpServer.Exceptions;

public class HttpServerException extends RuntimeException{
    public HttpServerException(String msg){
        super(msg);
    }
}
