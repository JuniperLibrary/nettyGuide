//package com.uin.netty.nativeinternet.jms;
//
//import javax.jms.*;
//import org.apache.activemq.ActiveMQConnectionFactory;
//
//public class JMSSample {
//  public static void main(String[] args) throws JMSException {
//    // Create a connection factory
//    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
//
//    // Create a connection
//    Connection connection = factory.createConnection();
//    connection.start();
//
//    // Create a session
//    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//    // Create a queue
//    Destination destination = session.createQueue("SAMPLE.QUEUE");
//
//    // Create a message producer
//    MessageProducer producer = session.createProducer(destination);
//    TextMessage message = session.createTextMessage("Hello, JMS!");
//    producer.send(message);
//    System.out.println("Sent message: " + message.getText());
//
//    // Create a message consumer
//    MessageConsumer consumer = session.createConsumer(destination);
//    Message receivedMessage = consumer.receive(1000);
//    if (receivedMessage instanceof TextMessage) {
//      TextMessage textMessage = (TextMessage) receivedMessage;
//      System.out.println("Received message: " + textMessage.getText());
//    }
//
//    // Clean up
//    session.close();
//    connection.close();
//  }
//}
