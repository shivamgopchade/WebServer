package com.shivam.httpServer.http;

import com.shivam.httpServer.Exceptions.HttpParsingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpVersionEnum {
    HTTP_1_1("HTTP/1.1",1,1),
    HTTP_2_1("HTTP/2.1",2,1),
    HTTP_3_1("HTTP/3.1",3,1);
    public final String LITERAL;
    public final int MAJOR;
    public final int MINOR;

    HttpVersionEnum(String literal,int major,int minor){
        this.LITERAL=literal;
        this.MAJOR=major;
        this.MINOR=minor;
    }

    private static final Pattern httpVersionRegexPattern=Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)");

    public static HttpVersionEnum getBestCompatibleVersion(String literalVersion) throws HttpParsingException {
        Matcher matcher=httpVersionRegexPattern.matcher(literalVersion);
        if(!matcher.find() || matcher.groupCount()!=2)
            throw  new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);

        int major=Integer.parseInt(matcher.group("major"));
        int minor=Integer.parseInt(matcher.group("minor"));

        HttpVersionEnum tempVersion=null;

        for(HttpVersionEnum version:HttpVersionEnum.values()){
            if(version.LITERAL.equals(literalVersion))
                return version;
            else{
                if(version.MAJOR==major){
                    if(version.MINOR<minor){
                        tempVersion=version;
                    }
                }
            }
        }
        return tempVersion;
    }
}
