package org.apache.commons.mail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.HandshakeCompletedEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class EmailTest {

    Email email;
    String[] emails = {"asd@123", "presidents@gmails", "hi@there", "validemail@valid"};
    String invalidEmail = "emails_xD";
    String[] noEmails = null;
    String validEmail = "valid@email";
    String name = "Name";
    
    @Before
    public void EmailSetup(){
        email = new EmailDummy();
    }

    @Test
    public void addBccTestValidEmail() throws EmailException {
        email.addBcc(emails);
        assertEquals(4, email.getBccAddresses().size());
    }

    @Test(expected = EmailException.class)
    public void addBccTestInvalidEmail() throws EmailException {
        email.addBcc(invalidEmail);
    }
    @Test(expected = EmailException.class)
    public void addBccTestNoEmails() throws EmailException {
        email.addBcc(noEmails);
    }

    @Test
    public void addCcValidEmail() throws EmailException {
        email.addCc(emails);
        assertEquals(4, email.getCcAddresses().size());
    }

    @Test(expected = EmailException.class)
    public void addCcTestInvalidEmail() throws EmailException {
        email.addCc(invalidEmail);
    }

    @Test(expected = EmailException.class)
    public void addCcTestNoEmails() throws EmailException {
        email.addCc(noEmails);
    }

    @Test
    public void addHeaderTest(){
        email.addHeader("Name", "Value");
        assertEquals("Value", email.headers.get("Name"));
    }

    @Test(expected =  IllegalArgumentException.class)
    public void addHeaderTestNoName(){
        email.addHeader(null, "Value");
    }

    @Test(expected =  IllegalArgumentException.class)
    public void addHeaderTestNoValue(){
        email.addHeader("Name", null);
    }

    @Test
    public void addReplyToTest() throws EmailException {
        email.addReplyTo(validEmail);
        assertEquals(1, email.getReplyToAddresses().size());
    }

    @Test
    public void buildMimeMessageTestFullInfo() throws EmailException, MessagingException, IOException{
    	MimeMultipart testMultipart = new MimeMultipart();
    	BodyPart testPart = new MimeBodyPart();
    	testPart.setText("Test Text");
    	testMultipart.addBodyPart(testPart);
    	email.setMsg("Hi there");
    	email.setSubject("Test Subject");
    	email.setFrom(validEmail, name);
    	email.addCc(emails);
    	email.setContent(testMultipart);
    	email.buildMimeMessage();
    	assertEquals(testMultipart, email.getMimeMessage().getContent());
    }
    @Test
    public void buildMimeMessageTestNoMessage() throws EmailException, MessagingException{
    	MimeMultipart testMultipart = new MimeMultipart();
    	BodyPart testPart = new MimeBodyPart();
    	testPart.setText("Test Text");
    	testMultipart.addBodyPart(testPart);
    	email.setSubject("Test Subject");
    	email.setFrom(validEmail, name);
    	email.addCc(emails);
    	email.addTo(emails);
    	email.addBcc(emails);
    	email.addReplyTo(validEmail);
    	email.content = "Text";
    	email.addHeader("Name", "Value");
    	email.buildMimeMessage();
    	assertEquals("Test Subject", email.getMimeMessage().getSubject());
    }
    
    @Test(expected = EmailException.class)
    public void buildMimeMessageTestNoParam() throws EmailException, MessagingException{
    	email.buildMimeMessage();
    }
    
    @Test(expected = EmailException.class)
    public void buildMimeMessageTestWithFrom() throws EmailException, MessagingException{
    	email.setFrom(validEmail, name);
    	email.buildMimeMessage();
    }
    

    @Test
    public void getHostNameWithSessionTest() throws EmailException {
        Session aSession = Session.getInstance(new Properties());
        email.setMailSession(aSession);
        email.getMailSession().getProperties().setProperty(email.MAIL_HOST, "Host");
        String temp = email.getHostName();
        assertEquals("Host", temp);
    }
    
    @Test
    public void getHostNameWithoutSessionTest() throws EmailException {
        email.setHostName("Host_Test");
        String temp = email.getHostName();
        assertEquals("Host_Test", temp);
    }

    @Test
    public void getMailSessionTestWithSession() throws EmailException {
        Session aSession = Session.getInstance(System.getProperties());
        email.setMailSession(aSession);
        email.getMailSession().getProperties().setProperty(email.MAIL_HOST, "HOST");
        assertEquals(aSession, email.getMailSession());
    }

    @Test
    public void getMailSessionTestWithoutSession() throws EmailException {
        email.getMailSession().getProperties().setProperty(email.MAIL_HOST, "Host");
        assertEquals("Host", email.getMailSession().getProperty(email.MAIL_HOST));
    }

    @Test(expected = EmailException.class)
    public void getMailSessionTestWithoutHost() throws EmailException {
        email.getMailSession();
    }

    @Test
    public void getSentDateTest(){
        assertEquals(new Date(), email.getSentDate());
    }

    @Test
    public void getSentDateTestWithDate(){
        email.setSentDate(new Date());
        assertEquals(new Date(), email.getSentDate());
    }

    @Test
    public void getSocketConnectionTimeoutTest(){
        assertEquals(60000, email.getSocketConnectionTimeout());
    }

    @Test
    public void setFromTest() throws EmailException, UnsupportedEncodingException {
        email.setFrom(validEmail, name);
        InternetAddress input = new InternetAddress();
        input.setPersonal(name);
        input.setAddress(validEmail);
        assertEquals(input, email.getFromAddress());
    }

    @Test
    public void setFromTestJustEmail() throws EmailException{
        email.setFrom(validEmail);
        InternetAddress input = new InternetAddress();
        input.setAddress(validEmail);
        assertEquals(input, email.getFromAddress());
    }

    @After
    public void EmailTeardown(){
        email = new EmailDummy();
    }
}
