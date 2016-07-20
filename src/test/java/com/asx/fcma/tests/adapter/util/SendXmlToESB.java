

package com.asx.fcma.tests.adapter.util;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.tibco.tibjms.TibjmsQueueConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import asx.esb.ESBInterface;

import com.asx.fcma.tests.adapter.config.ConfigManager;


/**
 * Created by kanchi_m on 11/20/2015.
 */
public class SendXmlToESB {

    // Getting the ESB related information from the environment file
    private String serverUrl = ConfigManager.getInstance().getEnvConfig().getEsbServerUrl();
    private String username = ConfigManager.getInstance().getEnvConfig().getEsbUsername();
    private String password = ConfigManager.getInstance().getEnvConfig().getEsbPassword();
    private String qName = ConfigManager.getInstance().getEnvConfig().getEsbQname();
    private String poisonQName = ConfigManager.getInstance().getEnvConfig().getPoisonQname();

    public String xmlToString(String xmlPath) throws IOException {
        XML xml = new XMLDocument(new File(xmlPath));
        String xmlString = xml.toString();
        return xmlString;
    }

    public QueueConnection getESBConnection () throws JMSException {
        QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl);
        QueueConnection connection = factory.createQueueConnection(username, password);
        return connection;
    }

    public void closeESBConnection (QueueConnection connection) throws JMSException {
        connection.close();
    }



    public void writeMessage(String message ,QueueConnection connection ) throws NamingException, JMSException, InterruptedException {
        //Sending the xml message to the bus

        try {
            Thread.sleep(1000);
            // QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl);
            //  QueueConnection connection = factory.createQueueConnection(username, password);
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(qName);
            QueueSender sender = session.createSender(queue);

            TextMessage mess = session.createTextMessage();

            mess.setJMSDeliveryMode(1);
            mess.setText(message);
            System.out.println("Sending the Input XML to ESB");
            sender.send(mess);
            sender.close();
            session.close();


        } catch (JMSException e) {
            e.printStackTrace();
            System.out.println("Unable to connect to ESB");
        }
    }

    //read the message from a specific queue
    public String readMessage1(QueueConnection connection) throws NamingException, JMSException, InterruptedException {
        List<Message> messagesList = new ArrayList<Message>();
        String outputXml[] = null;
        try {
            //    QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl);
            //    QueueConnection connection = factory.createQueueConnection(username, password);
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("asx.downstream.ci.adp.drps.instrument.poison");
            QueueReceiver queueReceiver = session.createReceiver(queue);
            connection.start();
            Message message = null;

            boolean exit = false;
            int wait = 10;

            while (!exit) {
                while ((message = queueReceiver.receiveNoWait()) != null) {
                    // message.getText();
                    messagesList.add(message);
                    exit = true;
                }
                if (message == null && wait <= 0) {
                    exit = true;
                }
                Thread.sleep(1000);
                wait--;
            }
            queueReceiver.close();
            session.close();

        } catch (JMSException e) {
            e.printStackTrace();
            System.out.println("Unable to connect to ESB");
        }
        for (Iterator<Message> iterator = messagesList.iterator(); iterator.hasNext(); ) {
            Message message = (Message) iterator.next();
            String queueMessage = message.toString();

            Thread.sleep(1000);
            String queueText[] = queueMessage.split("Text=\\{");
            outputXml = queueText[1].split("}");
        }

        if (outputXml == null){
            System.out.println("No Messages in the Queue");
            return null;
        }
        else{
            return outputXml[0];
        }

    }

    //read the message from a specific queue
    public List readMessage(QueueConnection connection) throws NamingException, JMSException, InterruptedException {
        List<Message> messagesList = new ArrayList<Message>();

        //    QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl);
        //    QueueConnection connection = factory.createQueueConnection(username, password);
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(poisonQName);
        QueueReceiver queueReceiver = session.createReceiver(queue);
        connection.start();
        Message message = null;
        boolean exit = false;
        int wait = 10;
        while (!exit) {
            while ((message = queueReceiver.receiveNoWait()) != null) {
                // message.getText();
                messagesList.add(message);
                exit = true;
            }
            if (message == null && wait <= 0) {
                exit = true;
            }
            Thread.sleep(1000);
            wait--;
        }
        queueReceiver.close();
        session.close();
        //  connection.close();
        return messagesList;
    }
}
