package com.rpms.NotificationsAndReminders;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Handles email notifications throughout the application.
 * Uses JavaMail API to send real emails through Gmail's SMTP server.
 */
public class EmailNotification implements Notifiable {
    /** Email address of the recipient */
    private String recipientEmail;
    
    /** Subject line of the email */
    private String subject;
    
    /** Body content of the email */
    private String message;

    /**
     * Constructor to create a new email notification.
     * Also automatically sends the email upon creation.
     * 
     * @param recipientEmail Email address of the recipient
     * @param subject Subject line of the email
     * @param message Body content of the email
     */
    public EmailNotification(String recipientEmail, String subject, String message) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
        
        // Auto-send the notification upon creation
        sendNotification();
    }

    // ===== Getters and Setters =====
    
    /**
     * Gets the recipient's email address.
     * 
     * @return Email address as a string
     */
    public String getRecipientEmail() { 
        return recipientEmail; 
    }
    
    /**
     * Sets the recipient's email address.
     * 
     * @param recipientEmail New email address to use
     */
    public void setRecipientEmail(String recipientEmail) { 
        this.recipientEmail = recipientEmail; 
    }
    
    /**
     * Gets the email subject.
     * 
     * @return Subject line as a string
     */
    public String getSubject() { 
        return subject; 
    }
    
    /**
     * Sets the email subject.
     * 
     * @param subject New subject line to use
     */
    public void setSubject(String subject) { 
        this.subject = subject; 
    }
    
    /**
     * Gets the email message body.
     * 
     * @return Message body as a string
     */
    public String getMessage() { 
        return message; 
    }
    
    /**
     * Sets the email message body.
     * 
     * @param message New message body to use
     */
    public void setMessage(String message) { 
        this.message = message; 
    }

    /**
     * Sends the email notification using JavaMail API.
     * Connects to Gmail's SMTP server to send the actual email.
     */
    @Override
    public void sendNotification() {
        // System email credentials
        final String senderEmail = "rpms502082.test@gmail.com"; // hard-coded email address
        final String senderPassword = "iuyl msru fgzu zobf"; // an app password (not a regular Gmail password)

        // Configure SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create authentication session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create and configure the email message
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(senderEmail));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            email.setSubject(subject);
            email.setText(message);

            // Send the email
            Transport.send(email);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            System.out.println("Failed to send email.");
            // Silently catch exception - we don't want to crash the application if email fails
        }
    }
}
