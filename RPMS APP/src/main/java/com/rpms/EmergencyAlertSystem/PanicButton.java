package com.rpms.EmergencyAlertSystem;

import com.rpms.Users.Patient;

/**
 * Implements the panic button functionality for emergency situations.
 * Allows patients to manually trigger emergency alerts with custom messages.
 */
public class PanicButton {
    /**
     * Simulates a patient pressing the panic button to trigger an emergency alert.
     * Calls the notification service to send alerts to physician and emergency contacts.
     * 
     * @param message Custom emergency message from the patient
     * @param patient The patient who activated the panic button
     */
    public static void pressPanicButton(String message, Patient patient) {
        // Create the full emergency message
        String fullMessage = "PANIC BUTTON PRESSED: " + message + 
                            "\nPatient: " + patient.getName() + 
                            " (ID: " + patient.getId() + ")";
        
        // Trigger emergency alert through the notification service
        NotificationService.sendAlert(fullMessage, patient);
        
        // Log to console for system monitoring
        System.out.println("Panic button pressed by " + patient.getName() + ": " + message);
    }
}
