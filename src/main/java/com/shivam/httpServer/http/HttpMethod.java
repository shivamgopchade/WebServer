package com.shivam.httpServer.http;

public enum HttpMethod {

    GET,
    HEAD,
    POST;

    public static final int MAX_LENGTH;

    static{
        int tempMaxLength=0;
        for(HttpMethod method:HttpMethod.values()){
            if(method.name().length()>tempMaxLength)
                tempMaxLength=method.name().length();
        }
        MAX_LENGTH=tempMaxLength;
    }
}
