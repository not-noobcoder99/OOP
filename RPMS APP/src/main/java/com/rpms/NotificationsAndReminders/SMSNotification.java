package com.rpms.NotificationsAndReminders;

/**
 * Handles SMS notifications throughout the application.
 * Currently implements a simulation of SMS sending via console output.
 * In a production environment, this would connect to an SMS gateway service.
 */
public class    SMSNotification implements Notifiable {
    /** Phone number to send the SMS to */
    private String phoneNumber;
    
    /** Content of the SMS message */
    private String message;

    /**
     * Constructor to create a new SMS notification.
     * Also automatically sends the notification upon creation.
     * 
     * @param phoneNumber Target phone number for the SMS
     * @param message Content of the SMS message
     */
    public SMSNotification(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        
        // Auto-send the notification upon creation
        sendNotification();
    }
    
    // ===== Getters and Setters =====
    
    /**
     * Gets the phone number this notification is addressed to.
     * 
     * @return The phone number as a string
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     * Sets the phone number for this notification.
     * 
     * @param phoneNumber New phone number to use
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     * Gets the SMS message content.
     * 
     * @return The message as a string
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the SMS message content.
     * 
     * @param message New message to use
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sends the SMS notification.
     * This is a simulation that logs the action to the console.
     * In a production system, this would use an SMS gateway API.
     */
    @Override
    public void sendNotification() {
        // This is a simulation of sending an SMS
        System.out.println("SMS sent to " + phoneNumber + ": " + message);
    }
}
