package com.asx.fcma.tests.adapter.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;

/**
 * Created by auto_test on 4/01/2016.
 */
public class GeniumXmlParser {

    public HashMap<String,String> parseXml(String xmlPath) throws Exception {

        String upperLevelSeries = null;
        HashMap<String,String> expected = new HashMap<String,String>();
        // Creating a hash map instance to store all retrieved elements from XML which will act as the list of expected values

        File fXmlFile = new File(xmlPath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        // Load the input XML document, parse it and return an instance of the Document class.

        Document document = builder.parse(fXmlFile);

        //Parsing the xml and populating the hash map
        expected.put("EXCHANGE_CODE", document.getElementsByTagName("ASX_MARKET_ID").item(0).getFirstChild().getNodeValue());

        //Identify display code for genium and downstream
        NodeList nodeList = document.getElementsByTagName("INSTR_IDENTIFIER");
        for (int i=0; i<nodeList.getLength();i++ ){
            Element elem = (Element)nodeList.item(i);

           if (elem.getElementsByTagName("INSTR_ID_TYPE").item(0).getTextContent().equalsIgnoreCase("GENIUM")){
               expected.put("GENIUM_DISPLAY_CODE",elem.getElementsByTagName("INSTR_ID_VALUE").item(0).getTextContent());
               expected.put("INSTRUMENT_SERIES",expected.get("GENIUM_DISPLAY_CODE"));
           }
            if (elem.getElementsByTagName("INSTR_ID_TYPE").item(0).getTextContent().equalsIgnoreCase("InstrumentClass")){
                expected.put("INSTRUMENT_CLASS",elem.getElementsByTagName("INSTR_ID_VALUE").item(0).getTextContent());
             }
            if (elem.getElementsByTagName("INSTR_ID_TYPE").item(0).getTextContent().equalsIgnoreCase("GENIUM_UNDERLYING")){
                upperLevelSeries = elem.getElementsByTagName("INSTR_ID_VALUE").item(0).getTextContent();
            }
        }
        String instrumentStatus = document.getElementsByTagName("INSTRUMENT_STATUS").item(0).getFirstChild()!= null ? document.getElementsByTagName("INSTRUMENT_STATUS").item(0).getFirstChild().getNodeValue():" ";
        if (instrumentStatus.equalsIgnoreCase("TR"))
        {
            expected.put("TRADED","Y");
        }
        else if(instrumentStatus.equalsIgnoreCase("NT"))
        {
            expected.put("TRADED","N");
        }
        //Parsing the generic fields which are common for both futures and options
        expected.put("FIRST_TRADING_DATE" ,document.getElementsByTagName("FIRST_ACTIVE_DATE").item(0).getFirstChild()!= null ? document.getElementsByTagName("FIRST_ACTIVE_DATE").item(0).getFirstChild().getNodeValue():" ");
        expected.put("LAST_TRADING_DATE" ,document.getElementsByTagName("LAST_ACTIVE_DATE").item(0).getFirstChild()!= null ? document.getElementsByTagName("LAST_ACTIVE_DATE").item(0).getFirstChild().getNodeValue():" ");
        expected.put("EXPIRY_DATE" ,document.getElementsByTagName("EXPIRY_DATE").item(0).getFirstChild()!= null ? document.getElementsByTagName("EXPIRY_DATE").item(0).getFirstChild().getNodeValue():" ");
        expected.put("PRICE_QUOTATION_FACTOR" ,document.getElementsByTagName("PRICE_QUOTATION_FACTOR").item(0).getFirstChild()!= null ? document.getElementsByTagName("PRICE_QUOTATION_FACTOR").item(0).getFirstChild().getNodeValue():" ");
        expected.put("CONTRACT_SIZE" ,document.getElementsByTagName("PRICE_QUOTATION_FACTOR").item(0).getFirstChild()!= null ? document.getElementsByTagName("PRICE_QUOTATION_FACTOR").item(0).getFirstChild().getNodeValue():" ");

        String securityTypeCode = document.getElementsByTagName("SECURITY_TYPE_CODE").item(0).getFirstChild().getNodeValue();
        //Parsing the Futures specific fields
        if (securityTypeCode.equalsIgnoreCase("97"))
        {
            expected.put("DERIVATIVE_LEVEL","1");
        }
        //Parsing the Options specific fields
        if (securityTypeCode.equalsIgnoreCase("94") || securityTypeCode.equalsIgnoreCase("96")) //Options XML
        {
            expected.put("DERIVATIVE_LEVEL","2");
            expected.put("STRIKE_PRICE" ,document.getElementsByTagName("EXERCISE_PRICE").item(0).getFirstChild()!= null ? document.getElementsByTagName("EXERCISE_PRICE").item(0).getFirstChild().getNodeValue():" ");
            expected.put("UPPER_LEVEL_SERIES",upperLevelSeries);
        }
        //Some of the fields are not mapped in XML and never change in Genium, These fields are below and are used for verification purpose only
        expected.put("Status", "Active");
        expected.put("CALCULATE_DATES","N");
        expected.put("AT_END_OF_TRADING","Y");
        expected.put("DERIVATIVE_LEVEL","1");
        return expected;
    }

}


