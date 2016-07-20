package com.asx.fcma.tests.adapter.definitions;

/**
 * Holds the step definitions for BDD scenarios
 * @author kanchi_m
 *
 *
 */
import com.asx.fcma.tests.adapter.util.*;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


import org.junit.Assert;
import org.unitils.reflectionassert.ReflectionAssert;


import javax.jms.QueueConnection;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class DownstreamStepDefs {

	HashMap<String,String> expected = new HashMap<String,String>();
	HashMap<String,String> actual = new HashMap<String,String>();
	String xmlPath = null;
	String downStreamDisplayCode = null;
	String tempFilePath = "src\\\\test\\\\resources\\\\data\\temp.xml";
	SendXmlToESB sendXmlToESB = new SendXmlToESB();

	static QueueConnection connection = null;
	static boolean dunit = false;

	@Given("^that I prepare the \"([^\"]*)\" for submission$")
	public void that_I_prepare_the_for_submission(String xml) throws Throwable {
		xmlPath = xml;
		DownStreamXmlParser downStreamXmlParser = new DownStreamXmlParser();
		downStreamXmlParser.buildXml(xml);
		expected = downStreamXmlParser.parseXml(xml);

	}

	@And("^I submit the input xml to ESB$")
	public void I_submit_the_input_xml_to_ESB() throws Throwable {
		SendXmlToESB sendXmlToESB = new SendXmlToESB();
		String message= sendXmlToESB.xmlToString(xmlPath);
		if (!dunit){
			connection = sendXmlToESB.getESBConnection();
			dunit = true;
			System.out.println("Connected to ESB");
		}
		sendXmlToESB.writeMessage(message,connection);
		Thread.sleep(1000);
	}

	@When("^I retrieve data from the Downstream db$")
	public void i_retrieve_data_from_the_Downstream_db() throws Throwable {
		RetrieveDataFromDb retrieveDataFromDb = new RetrieveDataFromDb();
		//Getting the type of XML (Futures or Options)
		String type = expected.get("UNDERLYING_CLASS");
		Connection conn = retrieveDataFromDb.getDBConnection();
		actual = retrieveDataFromDb.getData(conn, expected.get("DOWNSTREAM_DISPLAY_CODE"), type);
		//Since we are validating downstream data, we have to ignore genium display code
		expected.put("GENIUM_DISPLAY_CODE","ignore");
	}

	@Then("^the data should match to the xml submitted$")
	public void the_data_should_match_to_the_xml_submitted() throws Throwable {
		//Asserting the values of hash map from xml and the hash map from database, if both the data match then the xml was processed successfully
		System.out.println("Asserting values from XML and Downstream DB");
		ReflectionAssert.assertReflectionEquals(expected,actual);
	}

	@And("^I check that the error has been logged in the log file$")
	public void I_check_that_the_error_has_been_logged_in_the_log_file() throws Throwable {
		FileOperations fileOp = new FileOperations();
		if (fileOp.CheckErrorInLogFile()){} //Checking that an error has been logged in the log file.
		else{
			System.out.println("No error Found, File processed successfully");
		}
	}

	@Then("^the data should not be updated in the db$")
	public void the_data_should_not_be_updated_in_the_db() throws Throwable {
		System.out.println("Checking that the record is not updated in the db whenever an error occurs");
		Assert.assertNotEquals(expected.get("RECORD_MODIFICATION_DATE"), actual.get("RECORD_MODIFICATION_DATE"));
	}

	@Then("^there should be no data in db$")
	public void there_should_be_no_data_in_db() throws Throwable {
		RetrieveDataFromDb retrieveDataFromDb = new RetrieveDataFromDb();
		Connection conn = retrieveDataFromDb.getDBConnection();
		System.out.println("Verifying DB to see that there are no records found for the given display code");
		int NoOfRows = retrieveDataFromDb.getRowCountForDisplayCode(conn ,downStreamDisplayCode);
		Assert.assertEquals(0,NoOfRows);
	}

	@Given("^that I prepare the \"([^\"]*)\" for submission with \"([^\"]*)\" and \"([^\"]*)\"$")
	public void that_I_prepare_the_for_submission_with_and(String xml, String tagName, String value) throws Throwable {
		DownStreamXmlParser downStreamXmlParser = new DownStreamXmlParser();
		xmlPath = downStreamXmlParser.buildXmlForErrorCases(xml,tagName,value);
		downStreamDisplayCode = downStreamXmlParser.returnDownStreamDisplayCode(xmlPath);
	}

/*	@Given("^that I read the message from the queue$")
	public void that_I_read_the_message_from_the_queue() throws Throwable {

		String content = sendXmlToESB.readMessage();
		if (content != null) {
			File tempFile = new File(tempFilePath);
			FileOperations fileOp = new FileOperations();
			fileOp.convertStringToFile(tempFile, content);
		}
		else{
			System.out.println("No message in queue- cannot proceed further");
			System.exit(0);
		}
	} */

	@Then("^I should have my actual data$")
	public void I_should_have_my_actual_data() throws Throwable {
		NorthAdapterXmlParser northAdapterXmlParser = new NorthAdapterXmlParser();
		actual = northAdapterXmlParser.parseXml(tempFilePath);
		System.out.println(actual);

	}

	@And("^I clear the downstream Poison Queue$")
	public void I_clear_the_downstream_Poison_Queue() throws Throwable {
		// Read all the messages in the poison queue
		if (!dunit){
			connection = sendXmlToESB.getESBConnection();
			System.out.println("Connected to ESB");
			dunit = true;
		}
		sendXmlToESB.readMessage(connection);
	}

	@And("^the inputXml is in the poison Queue$")
	public void the_inputXml_is_in_the_poison_Queue() throws Throwable {
		if (!dunit){
			connection = sendXmlToESB.getESBConnection();
			System.out.println("Connected to ESB");
			dunit = true;
		}
		List messageList = sendXmlToESB.readMessage(connection);
		System.out.println("Checking that the error XML is stored in the poison Queue");
		Assert.assertEquals(1, messageList.size());
	}

	@Given("^I close all my active connections$")
	public void iCloseAllMyActiveConnections() throws Throwable {
		sendXmlToESB.closeESBConnection(connection);
		dunit = false;
		System.out.println("Disconnected to ESB");
	}
}