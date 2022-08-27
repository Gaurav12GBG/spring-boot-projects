package com.smart.smartcontactmanager.services;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmail(String subject, String message, String to){

       //Rest of code
        boolean flag = false;

        // Sender's email ID needs to be mentioned
        String from = "your-email";
        String password = "your-password";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();
        System.out.println("Properties: "+ properties);

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password


        Session session = javax.mail.Session.getInstance(properties, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(from, password);

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage m = new MimeMessage(session);

            // Set From: header field of the header.
            m.setFrom(from);

            // Set To: header field of the header.
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            m.setSubject(subject);

            // Now set the actual message
            // m.setText(message);
            m.setContent(message, "text/html");

            System.out.println("sending success...");
            // Send message
            Transport.send(m);

            System.out.println("Sent message successfully....");
            flag = true;


        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

        return flag;

      }
}
