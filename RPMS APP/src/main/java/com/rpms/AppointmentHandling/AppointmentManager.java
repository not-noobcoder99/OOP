package com.rpms.AppointmentHandling;

import java.util.ArrayList;

import com.rpms.ChatVideoConsultation.VideoCall;
import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.utilities.DataManager;

/**
 * Central manager for all appointments and video calls in the system.
 * Provides static methods to create, approve, and cancel appointments and video calls.
 * Maintains the central repository of all scheduled appointments and consultations.
 */
public class AppointmentManager {
    /** Central repository of all appointments in the system */
    private static ArrayList<Appointment> appointments = new ArrayList<>();
    
    /** Central repository of all video calls in the system */
    private static ArrayList<VideoCall> videoCalls = new ArrayList<>();

    /**
     * Gets all appointments in the system.
     * 
     * @return ArrayList of all appointments
     */
    public static ArrayList<Appointment> getAppointments() {
        return appointments;
    }
    
    /**
     * Gets all video calls in the system.
     * 
     * @return ArrayList of all video calls
     */
    public static ArrayList<VideoCall> getVideoCalls() {
        return videoCalls;
    }

    /**
     * Adds a new appointment request to the system.
     * Sets initial status to "Pending" until approved by a doctor.
     * 
     * @param appointment The appointment to request
     */
    public static void requestAppointment(Appointment appointment) {
        appointments.add(appointment);
        System.out.println("Appointment added to queue. Waiting to be approved.");
        DataManager.saveAllData(); // Auto-save
    }

    /**
     * Approves a pending appointment.
     * Changes status to "Approved" and adds the patient to the doctor's patient list.
     * Uses the authoritative doctor and patient objects from the Administrator's registry.
     * 
     * @param appointment The appointment to approve
     */
    public static void approveAppointment(Appointment appointment) {
        appointment.setStatus("Approved");
        System.out.println("Appointment approved: " + appointment.getDateTime());
        
        // Get the authoritative doctor from Administrator's list
        Doctor authoritativeDoctor = null;
        for (Doctor d : Administrator.getDoctors()) {
            if (d.getId().equals(appointment.getDoctor().getId())) {
                authoritativeDoctor = d;
                break;
            }
        }
        
        // Get the authoritative patient from Administrator's list
        Patient authoritativePatient = null;
        for (Patient p : Administrator.getPatients()) {
            if (p.getId().equals(appointment.getPatient().getId())) {
                authoritativePatient = p;
                break;
            }
        }
        
        // Only proceed if we found both
        if (authoritativeDoctor != null && authoritativePatient != null) {
            // Add patient to the doctor's list if not already there
            if (!authoritativeDoctor.getPatients().contains(authoritativePatient)) {
                authoritativeDoctor.addPatient(authoritativePatient);
                System.out.println("Patient " + authoritativePatient.getName() + 
                                  " added to Dr. " + authoritativeDoctor.getName() + "'s list");
            }
            
            // Update the appointment with these authoritative references
            appointment.setDoctor(authoritativeDoctor);
            appointment.setPatient(authoritativePatient);
        }
        
        DataManager.saveAllData(); // Auto-save
        System.out.println("Appointment approved for: " + appointment.getPatient().getName());
    }

    /**
     * Cancels an appointment.
     * Changes status to "Cancelled" but keeps it in the system for record-keeping.
     * 
     * @param appointment The appointment to cancel
     */
    public static void cancelAppointment(Appointment appointment) {
        appointment.setStatus("Cancelled");
        System.out.println("Appointment cancelled: " + appointment.getDateTime());
        DataManager.saveAllData(); // Auto-save
    }

    /**
     * Adds a new video call request to the system.
     * The call is set to "Pending" status until approved by a doctor.
     * 
     * @param videocall The video call to request
     */
    public static void requestVideoCall(VideoCall videocall) {
        videoCalls.add(videocall);
        System.out.println("Video call requested for appointment: " + videocall.getStartTime() + " to " + videocall.getEndTime());
        DataManager.saveAllData(); // Auto-save
    }

    /**
     * Cancels a video call.
     * Removes it from the active video calls list and sets status to "Cancelled".
     * 
     * @param videocall The video call to cancel
     */
    public static void cancelVideoCall(VideoCall videocall) {
        videoCalls.remove(videocall);
        videocall.setStatus("Cancelled");
        System.out.println("Video call cancelled for appointment: " + videocall.getStartTime() + " to " + videocall.getEndTime());
        DataManager.saveAllData(); // Auto-save
    }

    /**
     * Approves a video call request.
     * Changes status to "Approved" and adds the patient to the doctor's patient list if needed.
     * The doctor must provide a meeting link when approving.
     * 
     * @param videocall The video call to approve
     */
    public static void approveVideoCall(VideoCall videocall) {
        videocall.setStatus("Approved");
        System.out.println("VideoCall approved: " + videocall.toString());

        // Get the authoritative doctor from Administrator's list
        Doctor authoritativeDoctor = null;
        for (Doctor d : Administrator.getDoctors()) {
            if (d.getId().equals(videocall.getDoctor().getId())) {
                authoritativeDoctor = d;
                break;
            }
        }

        // Get the authoritative patient from Administrator's list
        Patient authoritativePatient = null;
        for (Patient p : Administrator.getPatients()) {
            if (p.getId().equals(videocall.getPatient().getId())) {
                authoritativePatient = p;
                break;
            }
        }

        // Only proceed if we found both
        if (authoritativeDoctor != null && authoritativePatient != null) {
            // Add patient to the doctor's list if not already there
            if (!authoritativeDoctor.getPatients().contains(authoritativePatient)) {
                authoritativeDoctor.addPatient(authoritativePatient);
                System.out.println("Patient " + authoritativePatient.getName() +
                        " added to Dr. " + authoritativeDoctor.getName() + "'s list");
            }

            // Update the appointment with these authoritative references
            videocall.setDoctor(authoritativeDoctor);
            videocall.setPatient(authoritativePatient);
        }

        DataManager.saveAllData(); // Auto-save
        System.out.println("Appointment approved for: " + videocall.getPatient().getName());
    } // Auto-save


    // no toString method is needed here because the appointment manager is not an object that needs to be printed
}
