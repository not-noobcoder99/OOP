package com.rpms.NotificationsAndReminders;

import com.rpms.AppointmentHandling.*;
import com.rpms.DoctorPatientInteraction.*;
import com.rpms.Users.*;
import com.rpms.ChatVideoConsultation.VideoCall;

/**
 * Service for managing various types of reminders for users.
 * Handles appointment reminders, medication reminders, and video call reminders.
 * Can send reminders to both patients and doctors through SMS and email.
 */
public class ReminderService {
    /** The user (patient or doctor) to send reminders to */
    User user;

    /**
     * Constructor for creating a reminder service for a patient.
     * 
     * @param patient The patient to receive reminders
     */
    public ReminderService(Patient patient) {
        this.user = patient;
    }
    
    /**
     * Constructor for creating a reminder service for a doctor.
     * 
     * @param doctor The doctor to receive reminders
     */
    public ReminderService(Doctor doctor) {
        this.user = doctor;
    }

    /**
     * Gets the user this reminder service is for.
     * 
     * @return The user (patient or doctor)
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets all upcoming approved appointments for the user.
     * Also sends SMS and email notifications with the appointment details.
     * 
     * @return String containing all upcoming appointment information
     */
    public String getAppointmentReminders() {
        StringBuilder sb = new StringBuilder();
        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if ((appointment.getPatient().equals(user) || appointment.getDoctor().equals(user)) 
                && appointment.getStatus().equals("Approved")) {
                // Only include future appointments
                if (appointment.getDateTime().isAfter(java.time.LocalDateTime.now())) {
                    sb.append(appointment).append("\n\n");
                }
            }
        }
        
        // Only send notifications if there are appointments to remind about
        if (sb.length() > 0) {
            // sending email/sms alert to the user
            new SMSNotification(this.getUser().getPhoneNumber(), sb.toString());
            new EmailNotification(this.getUser().getEmail(), "Appointment Reminder", sb.toString());
        }
        
        return sb.toString();
    }

    /**
     * Gets all medication reminders for the user.
     * Only works for patients as doctors don't have medications.
     * Also sends SMS and email notifications with the medication details.
     * 
     * @return String containing all medication information
     */
    public String getMedicationReminders() {
        // Early return if the user is not a patient
        if (!(user instanceof Patient patient)) return "";

        StringBuilder sb = new StringBuilder();
        for (Feedback fb : patient.getFeedbacks()) {
            for (Prescription prescription : fb.getPrescriptions()) {
                sb.append(prescription).append("\n");
            }
        }
        
        // Only send notifications if there are medications to remind about
        if (sb.length() > 0) {
            // sending email/sms alert to the patient
            new SMSNotification(this.getUser().getPhoneNumber(), sb.toString());
            new EmailNotification(this.getUser().getEmail(), "Medication Reminder", sb.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * Gets upcoming approved video calls for the user.
     * Also sends SMS and email notifications with the video call details.
     * 
     * @return String containing all approved video calls information
     */
    public String getApprovedVideoCalls() {
        StringBuilder sb = new StringBuilder();
        for (VideoCall videoCall : AppointmentManager.getVideoCalls()) {
            if ((videoCall.getPatient().equals(user) || videoCall.getDoctor().equals(user)) 
                    && videoCall.getStatus().equals("Approved")) {
                // Only include upcoming video calls
                if (videoCall.getStartTime().isAfter(java.time.LocalDateTime.now())) {
                    sb.append(videoCall).append("\n\n");
                }
            }
        }
        
        // If there are approved video calls, send notifications
        if (sb.length() > 0) {
            // sending email/sms alert to the user
            new SMSNotification(this.getUser().getPhoneNumber(), sb.toString());
            new EmailNotification(this.getUser().getEmail(), "Upcoming Video Consultations", sb.toString());
        }
        
        return sb.toString();
    }
}
