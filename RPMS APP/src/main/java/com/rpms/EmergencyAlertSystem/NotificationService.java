package com.rpms.EmergencyAlertSystem;

import com.rpms.NotificationsAndReminders.EmailNotification;
import com.rpms.NotificationsAndReminders.SMSNotification;
import com.rpms.Users.Patient;

/**
 * Delivers emergency notifications to appropriate parties.
 * Provides methods to send alerts via SMS and email to physicians and emergency contacts.
 */
public class NotificationService {

    /**
     * Sends SMS alerts to a patient's physician and all emergency contacts.
     * Creates and dispatches SMS notification objects with the emergency message.
     * 
     * @param message The emergency message to send
     * @param patient The patient experiencing the emergency
     */
    public static void sendSMS(String message, Patient patient) {
        // Creating new SMS object for the physician
        SMSNotification sms = new SMSNotification(patient.getPhysician().getPhoneNumber(), message);
        // Sending it
        sms.sendNotification();

        // Doing the same for all emergency contacts
        for(String contact : patient.getEmergencyContacts()) {
            SMSNotification emergencySms = new SMSNotification(contact, message);
            emergencySms.sendNotification();
        }
    }

    /**
     * Sends email alerts to a patient's physician and all emergency contacts.
     * Creates and dispatches email notification objects with the emergency information.
     * 
     * @param subject The email subject line
     * @param message The emergency message content
     * @param patient The patient experiencing the emergency
     */
    public static void sendEmail(String subject, String message, Patient patient) {
        // Creating new Email object for the physician
        EmailNotification email = new EmailNotification(patient.getPhysician().getEmail(), subject, message);
        // Sending it
        email.sendNotification();
        
        // Doing the same for all emergency contacts
        for(String contact : patient.getEmergencyContacts()) {
            EmailNotification emergencyEmail = new EmailNotification(contact, subject, message);
            emergencyEmail.sendNotification();
        }
    }

    /**
     * Sends both SMS and email alerts for an emergency situation.
     * This is the main method called by other components to trigger a full alert.
     * 
     * @param message The emergency message to send
     * @param patient The patient experiencing the emergency
     */
    public static void sendAlert(String message, Patient patient) {
        // Sending SMS and email to the physician and emergency contacts
        sendSMS(message, patient);
        sendEmail("Emergency Alert", message, patient);
    }
}
