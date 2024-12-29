package com.shivam.httpServer.http;

import com.shivam.httpServer.Exceptions.HttpParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {

    private static final int SP=0x20; //32
    private static final int CR=0xD; //13
    private static final int LF=0xA; //10
    private static final int OPEN_CURL=0x7B; //123
    private static final int CLOSE_CURL=0x7D; //125
    private static final int COMMA=0x2C; // 44

    public HttpRequest parseHttpRequest(InputStream is) throws IOException, HttpParsingException {
        try{
            InputStreamReader inputStreamReader=new InputStreamReader(is, StandardCharsets.US_ASCII);

            HttpRequest httpRequest=new HttpRequest();

            parseRequestLine(inputStreamReader,httpRequest);
            parseHeaders(inputStreamReader,httpRequest);
            if(httpRequest.getMethod()==HttpMethod.POST)
                parseBody(inputStreamReader,httpRequest);

            return httpRequest;
        }catch (Exception e){
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

    }

    private void parseRequestLine(InputStreamReader reader,HttpRequest httpRequest) throws IOException, HttpParsingException {
        int _byte;
        boolean methodParsed = false;
        boolean targetParsed = false;
        boolean versionParsed = false;
        StringBuilder processingBuffer = new StringBuilder();
        while ((_byte = reader.read()) != -1) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    if (methodParsed && targetParsed) {
                        //todo http version
                        httpRequest.setOriginalHttpVersion(processingBuffer.toString());
                        return;
                    } else
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

                    //httpRequest.setOriginalHttpVersion(processingBuffer.toString());
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            if (_byte == SP) {
                if (!methodParsed) {
                    //todo method type
                    httpRequest.setMethod(processingBuffer.toString());
                    methodParsed = true;
                } else if (!targetParsed) {
                    //todo req target
                    if (processingBuffer != null && !processingBuffer.isEmpty()) {
                        httpRequest.setTarget(processingBuffer.toString());
                        targetParsed = true;
                    }
                }
                else throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
                processingBuffer.delete(0, processingBuffer.length());
            } else {
                    if (!methodParsed && processingBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_401_METHOD_NOT_ALLOWED);
                    }
                    processingBuffer.append((char) _byte);
                }
            }
        }


    private void parseHeaders(InputStreamReader reader,HttpRequest httpRequest) throws IOException, HttpParsingException {
        int _byte;
        StringBuilder processingBuffer = new StringBuilder();
        while((_byte=reader.read())!=-1){
            if(_byte==CR){
                _byte=reader.read();
                if(_byte==LF){
                    String headerVal=processingBuffer.toString();

                    if(headerVal.isEmpty())
                        return;

                    processingBuffer.delete(0,processingBuffer.length());
                    processHeader(headerVal,httpRequest);
                }else{
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            else processingBuffer.append((char)_byte);
        }
    }

    private void processHeader(String singleHeader,HttpRequest httpRequest) throws HttpParsingException {
        String rawHeaderField=singleHeader;
        Pattern pattern = Pattern.compile("^(?<fieldName>[!#$%&’*+\\-./^_‘|˜\\dA-Za-z]+):\\s?(?<fieldValue>[!#$%&’*+\\-./^_‘|˜(),:;<=>?@[\\\\]{}\" \\dA-Za-z]+)\\s?$");

        Matcher matcher=pattern.matcher(singleHeader);
        if(matcher.matches()){
            String field=matcher.group("fieldName");
            String val=matcher.group("fieldValue");
            httpRequest.addHeader(field.toLowerCase(),val);
        }else throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

    }

    // body parsing for content-type json with ',' separated
    private void parseBody(InputStreamReader reader,HttpRequest httpRequest) throws IOException, HttpParsingException {
        int _byte=0;
        StringBuilder processingBuffer = new StringBuilder();
        while((_byte=reader.read())!=-1){
            if(_byte==OPEN_CURL || _byte==CR || _byte==LF || _byte==SP){
                continue;
            }
            else if(_byte==COMMA || _byte==CLOSE_CURL){
                String keyVal=processingBuffer.toString();
                processBody(keyVal,httpRequest);
                processingBuffer.delete(0,processingBuffer.length());
            }
            else{
                processingBuffer.append((char)_byte);
            }
        }
    }

    private void processBody(String keyVal,HttpRequest httpRequest) throws HttpParsingException {
        Pattern pattern = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"(.*?)\"");

        Matcher matcher=pattern.matcher(keyVal);
        if(matcher.matches()){
            String key=matcher.group(1);
            String val=matcher.group(2);
            httpRequest.addBody(key,val);
        }else throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
}
