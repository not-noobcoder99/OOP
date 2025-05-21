package com.rpms.EmergencyAlertSystem;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.rpms.HealthData.VitalSign;
import com.rpms.Users.Patient;

/**
 * Handles detection and processing of critical vital signs in the system.
 * Contains static methods for evaluating vital signs and triggering alerts when necessary.
 */
public class EmergencyAlert implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** The patient who raised the emergency alert */
    private final Patient patient;
    
    /** The emergency message */
    private final String message;
    
    /** The timestamp when the alert was raised */
    private final LocalDateTime timestamp;
    
    /** The status of the alert (Pending, Acknowledged, Resolved) */
    private String status;
    
    /** The response message from medical staff, if any */
    private String response;
    
    /**
     * Creates a new emergency alert with the specified details
     *
     * @param patient The patient who raised the alert
     * @param message The emergency message
     */
    public EmergencyAlert(Patient patient, String message) {
        this.patient = patient;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.status = "Pending";
    }
    
    // Getters
    
    /**
     * Gets the patient who raised the alert
     * @return Patient object
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * Gets the emergency message
     * @return Message string
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the timestamp when the alert was raised
     * @return LocalDateTime object
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the current status of the alert
     * @return Status string
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Gets the response to the alert
     * @return Response message
     */
    public String getResponse() {
        return response;
    }
    
    // Setters
    
    /**
     * Sets the status of the alert
     * @param status New status (Pending, Acknowledged, or Resolved)
     */
    public void setStatus(String status) {
        if (status.equals("Pending") || status.equals("Acknowledged") || status.equals("Resolved")) {
            this.status = status;
        }
    }
    
    /**
     * Sets a response message for the alert
     * @param response Response message from medical staff
     */
    public void setResponse(String response) {
        this.response = response;
        
        // Automatically update status to Acknowledged when a response is provided
        if (this.status.equals("Pending")) {
            this.status = "Acknowledged";
        }
    }
    
    /**
     * Checks if a vital sign reading is abnormal and sends alerts if necessary.
     * This method evaluates the given vital sign against medical thresholds,
     * and if abnormal, sends notifications to the patient's physician and emergency contacts.
     * 
     * @param patient The patient whose vital sign is being checked
     * @param vital The vital sign to evaluate
     * @return Alert message if abnormal, null if normal
     */
    public static String checkVitalSigns(Patient patient, VitalSign vital) {
        if (isVitalSignAbnormal(vital)) {
            String alertMsg = "Emergency Alert: Abnormal vital signs detected for Patient "
                    + patient.getName() + " (ID: " + patient.getId() + ")";
            NotificationService.sendAlert(alertMsg, patient);
            return alertMsg;
        }
        return null;
    }

    /**
     * Determines if a vital sign is outside normal medical ranges.
     * Evaluates heart rate, oxygen level, blood pressure, and temperature
     * against established medical thresholds.
     * 
     * @param vital The vital sign to evaluate
     * @return true if any measurement is abnormal, false if all are normal
     */
    public static boolean isVitalSignAbnormal(VitalSign vital) {
        // Heart rate outside normal range (60-100 bpm)
        if (vital.getHeartRate() < 60 || vital.getHeartRate() > 100) return true;
        
        // Oxygen saturation below 90% (hypoxemia)
        if (vital.getOxygenLevel() < 90) return true;

        // Blood pressure outside normal range
        String[] bpParts = vital.getBloodPressure().split("/");
        int systolic = Integer.parseInt(bpParts[0]);
        int diastolic = Integer.parseInt(bpParts[1]);
        if (systolic < 90 || systolic > 140 || diastolic < 60 || diastolic > 90) return true;

        // Temperature outside normal range (36.1-37.2°C)
        if (vital.getTemperature() < 36.1 || vital.getTemperature() > 37.2) return true;

        return false; // all normal
    }
}
