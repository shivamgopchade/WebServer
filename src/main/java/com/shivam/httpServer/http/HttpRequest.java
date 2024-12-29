package com.shivam.httpServer.http;

import com.shivam.httpServer.Exceptions.HttpParsingException;
import com.shivam.httpServer.Exceptions.HttpServerException;

import java.util.HashMap;
import java.util.Objects;

public class HttpRequest extends HttpMessage{

    private HttpMethod method;
    private String target;
    private String originalHttpVersion; // literal from the request
    private HttpVersionEnum bestCompatibleVersion;

    private HashMap<String,String> headers;

    private HashMap<String,String> body;

    public HttpRequest() {
    }

    public void setOriginalHttpVersion(String originalHttpVersion) throws HttpParsingException {
        this.originalHttpVersion=originalHttpVersion;
        this.bestCompatibleVersion=HttpVersionEnum.getBestCompatibleVersion(originalHttpVersion);
        if(this.bestCompatibleVersion==null)
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
    }

    public HttpMethod getMethod() {
        return method;
    }

    void setMethod(String method) throws HttpParsingException {
        for(HttpMethod httpMethod:HttpMethod.values()){
            if(method.equals(httpMethod.name())){
                this.method = HttpMethod.valueOf(method);
                return;
            }
        }
        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_401_METHOD_NOT_ALLOWED);

    }

    public HashMap<String,String> getBody(){
        return this.body;
    }

    void setTarget(String target){
        this.target=target;

    }

    public String getTarget(){
        return this.target;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key,String val){
        if(this.headers==null)
            this.headers=new HashMap<>();
        this.headers.put(key,val);
    }

    public void addBody(String Key,String value){
        if(this.body==null)
            this.body=new HashMap<>();
        this.body.put(Key,value);
    }
}
