package com.rpms.Users;

// Required imports
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.rpms.AppointmentHandling.Appointment;
import com.rpms.AppointmentHandling.AppointmentManager;
import com.rpms.ChatVideoConsultation.VideoCall;
import com.rpms.DoctorPatientInteraction.Feedback;
import com.rpms.EmergencyAlertSystem.EmergencyAlert;
import com.rpms.HealthData.VitalSign;
import com.rpms.HealthData.VitalsDatabase;
import com.rpms.utilities.DataManager;

/**
 * Patient class representing a patient in the healthcare system.
 * Extends the User class with patient-specific functionality.
 */
public class Patient extends User {
    /** Database to store the vital signs of the patient */
    private  VitalsDatabase vitalsDatabase;
    
    /** List to store previous feedback given by doctors */
    private final ArrayList<Feedback> feedbacks;
    
    /** List of emergency contact numbers for this patient */
    private ArrayList<String> emergencyContacts;
    
    /** Primary physician assigned to this patient */
    private Doctor physician;
    
    /** List to store emergency alerts for this patient */
    private ArrayList<EmergencyAlert> emergencyAlerts;

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to initialize a new Patient with all required fields
     * 
     * @param id Unique identifier for the patient
     * @param name Full name of the patient
     * @param phoneNumber Contact phone number
     * @param email Email address
     * @param username Username for login
     * @param password Password for authentication
     * @param emergencyContacts List of emergency contacts
     * @param physician Primary physician assigned to this patient
     */
    public Patient(String id, String name, String phoneNumber, String email, String username, String password, 
                  ArrayList<String> emergencyContacts, Doctor physician) {
        super(id, name, phoneNumber, email, username, password);
        // Initialize the vitals database and feedback list
        this.vitalsDatabase = new VitalsDatabase();
        this.feedbacks = new ArrayList<>();
        // New ArrayList for emergency contacts for each patient
        this.emergencyContacts = emergencyContacts;
        // New ArrayList for emergency alerts for each patient
        this.emergencyAlerts = new ArrayList<>();
        // Setting the physician for the patient
        this.physician = physician;
        // Remove duplicate assignment
        // Setting the physician for the patient
        // Safe check before adding
        if (physician != null) {
            physician.addPatient(this);
        }
    }

    /**
     * Returns the role of this user
     * @return "Patient" as the role
     */
    @Override
    public String getRole() {
        return "Patient";
    }

    /**
     * Gets the patient's username
     * 
     * @return The username
     */
    @Override
    public String getUsername() {
        return super.getUsername();
    }

    // ===== Getters and Setters =====
    
    /**
     * Gets the vital signs database for this patient
     * @return VitalsDatabase object
     */
    public VitalsDatabase getVitals() {
        return vitalsDatabase;
    }
    // No setter for vitalsDatabase as it's final

    /**
     * Gets the list of feedback given to this patient
     * @return ArrayList of Feedback objects
     */
    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }
    // No setter for feedbacks as it's final
    
    /**
     * Gets the list of emergency contacts for this patient
     * @return ArrayList of emergency contact numbers
     */
    public ArrayList<String> getEmergencyContacts() {
        return emergencyContacts;
    }

    
    /**
     * Gets the primary physician assigned to this patient
     * @return Doctor object representing the primary physician
     */
    public Doctor getPhysician() {
        return physician;
    }
    
    /**
     * Sets the patient's primary physician
     * @param physician The doctor to assign as the patient's physician
     */
    public void setPhysician(Doctor physician) {
        this.physician = physician;
        
        // Update the physician's patient list if needed
        if (!physician.getPatients().contains(this)) {
            physician.addPatient(this);
        }
    }

    // ===== Emergency Contact Methods =====
    
    /**
     * Adds a new emergency contact for this patient
     * @param contact New emergency contact number
     */
    public void addEmergencyContact(String contact) {
        if (!emergencyContacts.contains(contact)) {
            emergencyContacts.add(contact);
            System.out.println("Emergency contact added for patient: " + getName());
        } else {
            System.out.println("This contact already exists for patient: " + getName());
        }
    }
    
    /**
     * Removes an emergency contact for this patient
     * @param contact Emergency contact number to remove
     */
    public void removeEmergencyContact(String contact) {
        if (emergencyContacts.remove(contact)) {
            System.out.println("Emergency contact removed for patient: " + getName());
        } else {
            System.out.println("Contact not found for patient: " + getName());
        }
    }

    // ===== Emergency Alert Methods =====
    
    /**
     * Activates the panic button to send emergency alerts
     * @param message Message to include in the emergency alert
     */
    public void panicButton(String message) {
        // Add emergency alert to patient's record
        EmergencyAlert alert = addEmergencyAlert(message);
        
        // Trigger notification system
        com.rpms.EmergencyAlertSystem.PanicButton.pressPanicButton(message, this);
    }
    
    /**
     * Gets all emergency alerts raised by this patient
     * 
     * @return List of emergency alerts
     */
    public ArrayList<EmergencyAlert> getEmergencyAlerts() {
        return emergencyAlerts;
    }

    /**
     * Adds a new emergency alert for this patient
     * 
     * @param message Emergency message from the patient
     * @return The created emergency alert
     */
    public EmergencyAlert addEmergencyAlert(String message) {
        EmergencyAlert alert = new EmergencyAlert(this, message);
        emergencyAlerts.add(alert);
        return alert;
    }

    // ===== Vital Signs Methods =====
    
    /**
     * Uploads a new vital sign for this patient
     * @param vital VitalSign object to upload
     * @return String containing any health alerts triggered
     */
    public String uploadVitalSign(VitalSign vital) {
        vitalsDatabase.addVital(vital);
        System.out.println("Vital signs uploaded for patient: " + getName());
        
        // Check for critical values
        String alertMsg = EmergencyAlert.checkVitalSigns(this, vital);
        
        DataManager.savePatient(this); // Auto-save
        DataManager.saveAllData(); // Auto-save all data
        
        return alertMsg;
    }
    
    /**
     * Uploads multiple vital signs from a CSV file
     * @param filePath Path to the CSV file
     * @return ArrayList of alert messages for critical vitals
     */
    public ArrayList<String> uploadVitalsFromCSV(String filePath) {
        ArrayList<String> alerts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Skip header line if present
            if ((line = br.readLine()) != null) {
                // Skip header
            }

            // Process each line in the CSV file
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 5) {
                    System.out.println("Invalid data format: " + line);
                    continue;
                }

                try {
                    double heartRate = Double.parseDouble(data[0].trim());
                    double oxygenLevel = Double.parseDouble(data[1].trim());
                    String bloodPressure = data[2].trim();
                    double temperature = Double.parseDouble(data[3].trim());
                    LocalDateTime dateTimeRecorded = LocalDateTime.parse(data[4].trim());

                    VitalSign vital = new VitalSign(this.getId(), heartRate, oxygenLevel, bloodPressure, 
                                                  temperature, dateTimeRecorded);
                    String alert = uploadVitalSign(vital);
                    if (!alert.isEmpty()) {
                        alerts.add(alert);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing numeric values: " + line);
                } catch (Exception e) {
                    System.out.println("Error processing line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }

        System.out.println("Vitals uploaded from CSV for patient: " + getName());
        DataManager.savePatient(this); // Auto-save
        DataManager.saveAllData(); // Auto-save all data
        return alerts;
    }
    
    /**
     * Removes a vital sign from this patient's records
     * @param vitalSign VitalSign to remove
     */
    public void removeVital(VitalSign vitalSign) {
        vitalsDatabase.removeVital(vitalSign);
        DataManager.savePatient(this); // Auto-save
        DataManager.saveAllData(); // Auto-save all data
    }
    
    /**
     * Gets all previous vital signs for this patient
     * @return ArrayList of VitalSign objects
     */
    public ArrayList<VitalSign> viewPreviousVitals() {
        return vitalsDatabase.getVitals();
    }

    // ===== Feedback Methods =====
    
    /**
     * Adds a new feedback to this patient's records
     * @param feedback Feedback to add
     */
    public void addFeedback(Feedback feedback) {
        feedbacks.add(feedback);
        System.out.println("Feedback added for patient: " + getName());
        DataManager.savePatient(this); // Auto-save
        DataManager.saveAllData(); // Auto-save all data
    }

    
    /**
     * Gets all previous feedback for this patient
     * @return ArrayList of Feedback objects
     */
    public ArrayList<Feedback> viewPreviousFeedbacks() {
        ArrayList<Feedback> feedback = new ArrayList<>();
        for (Feedback f : feedbacks) {
            feedback.add(f);
        }
        return feedback;
    }

    // ===== Appointment Methods =====
    
    /**
     * Requests a new appointment for this patient
     * @param appointment Appointment to request
     */
    public void requestAppointment(Appointment appointment) {
        AppointmentManager.requestAppointment(appointment);
        System.out.println("Appointment requested for: " + getName());
    }
    
    /**
     * Gets all appointments for this patient
     * @return ArrayList of Appointment objects
     */
    public ArrayList<Appointment> viewAppointments() {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment appt : AppointmentManager.getAppointments()) {
            if (appt.getPatient().equals(this)) {
                result.add(appt);
            }
        }
        return result;
    }
    
    /**
     * Cancels an appointment for this patient
     * @param appt Appointment to cancel
     * @return true if successfully cancelled, false otherwise
     */
    public boolean cancelAppointment(Appointment appt) {
        if (appt.getPatient().equals(this)) {
            return AppointmentManager.getAppointments().remove(appt);
        }
        return false;
    }

    // ===== Video Call Methods =====
    
    /**
     * Requests a new video call for this patient
     * @param videocall VideoCall to request
     */
    public void requestVideoCall(VideoCall videocall) {
        AppointmentManager.requestVideoCall(videocall);
    }
    
    /**
     * Gets all video calls for this patient
     * @return ArrayList of VideoCall objects
     */
    public ArrayList<VideoCall> viewVideoCalls() {
        ArrayList<VideoCall> result = new ArrayList<>();
        for (VideoCall videoCall : AppointmentManager.getVideoCalls()) {
            if (videoCall.getPatient().equals(this)) {
                result.add(videoCall);
            }
        }
        return result;
    }
    
    /**
     * Cancels a video call for this patient
     * @param videoCall VideoCall to cancel
     * @return true if successfully cancelled, false otherwise
     */
    public boolean cancelVideoCall(VideoCall videoCall) {
        if (videoCall.getPatient().equals(this)) {
            return AppointmentManager.getVideoCalls().remove(videoCall);
        }
        return false;
    }

    // ===== Object Overrides =====
    
    /**
     * Checks if this patient equals another patient based on ID
     * @param obj The object to compare with
     * @return true if IDs match, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Patient other = (Patient) obj;
        return this.getId().equals(other.getId());
    }
}
