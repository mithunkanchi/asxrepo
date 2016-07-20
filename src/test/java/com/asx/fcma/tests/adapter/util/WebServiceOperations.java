package com.asx.fcma.tests.adapter.util;


import java.io.File;
import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;



/**
 * Created by kanchi_m on 29/01/2016.
 */
public class WebServiceOperations {

    public int postSoap() throws IOException
    {
        // Target URL of web service
        String strURL = "http://qgsas102:2051/standardvddb/webservice/Events";
        // Soap Action header parameter
        String strSoapAction = "http://www.thegoldensource.com/EventRaiserService.wsdl";
        // Body of Request in xml format
        String strXMLFilename = "src\\\\test\\\\resources\\\\data\\\\Sample.xml";
        //Response result
        int result = 0;

        File input = new File(strXMLFilename);
        // Prepare HTTP post
        PostMethod post = new PostMethod(strURL);
        // Request content will be retrieved directly
        // from the input stream
        RequestEntity entity = new FileRequestEntity(input, "text/xml; charset=UTF-8");
        post.setRequestEntity(entity);
        // consult documentation for your web service
        post.setRequestHeader("SOAPAction", strSoapAction);
        // Get HTTP client
        HttpClient httpclient = new HttpClient();
        // Execute request
        try {
            result = httpclient.executeMethod(post);
            // Display status code
            System.out.println("Response status code: " + result);
            // Display response
            System.out.println("Response body: ");
            System.out.println(post.getResponseBodyAsString());
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
        return result;
    }
}