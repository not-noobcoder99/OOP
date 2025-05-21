package com.rpms.AppointmentHandling;

import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.Users.Administrator;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.rpms.utilities.DateUtil;

/**
 * Represents a medical appointment between a doctor and a patient.
 * Tracks appointment details including date/time, status, and participants.
 */
public class Appointment implements Serializable {
    /** Date and time when the appointment is scheduled */
    private LocalDateTime datetime;
    
    /** Current status of the appointment (Pending, Approved, or Cancelled) */
    private String status;
    
    /** The doctor conducting the appointment */
    private Doctor doctor;
    
    /** The patient attending the appointment */
    private Patient patient;

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new appointment with the specified details.
     * Status is automatically set to "Pending" until approved by a doctor.
     * 
     * @param datetime Date and time of the appointment
     * @param doctor Doctor conducting the appointment
     * @param patient Patient attending the appointment
     */
    public Appointment(LocalDateTime datetime, Doctor doctor, Patient patient) {
        this.datetime = datetime;
        // using setters to ensure validation
        setDoctor(doctor);
        setPatient(patient);
        setStatus("Pending");
    }

    // ===== Getters =====
    
    /**
     * Gets the date and time of the appointment
     * @return DateTime of the appointment
     */
    public LocalDateTime getDateTime() { return datetime; }
    
    /**
     * Gets the current status of the appointment
     * @return Status string (Pending, Approved, or Cancelled)
     */
    public String getStatus() { return status; }
    
    /**
     * Gets the doctor conducting the appointment
     * @return Doctor object
     */
    public Doctor getDoctor() { return doctor; }
    
    /**
     * Gets the patient attending the appointment
     * @return Patient object
     */
    public Patient getPatient() { return patient; }

    // ===== Setters =====
    

    /**
     * Sets the status of the appointment with validation.
     * Only accepts valid status values: "Pending", "Approved", or "Cancelled".
     * 
     * @param status New appointment status
     */
    public void setStatus(String status) {
        // validation for states of status
        if (status.equals("Pending") || status.equals("Approved") || status.equals("Cancelled")) {
            this.status = status;
        } else {
            System.out.println("Invalid status. Status must be either 'Pending', 'Approved', or 'Cancelled'.");
        }
    }
    
    /**
     * Sets the doctor for this appointment.
     * Verifies the doctor exists in the system's registry.
     * 
     * @param doctor Doctor to assign to this appointment
     */
    public void setDoctor(Doctor doctor) {
        // checking if the doctor exists in the hospital
        for (Doctor d : Administrator.getDoctors()) {
            if (d.equals(doctor)) {
                this.doctor = doctor;
            }
        }
    }
    
    /**
     * Sets the patient for this appointment.
     * Verifies the patient exists in the system's registry.
     * 
     * @param patient Patient to assign to this appointment
     */
    public void setPatient(Patient patient) {
        // checking if the patient exists in the hospital
        for (Patient p : Administrator.getPatients()) {
            if (p.equals(patient)) {
                this.patient = patient;
            }
        }
    }

    /**
     * Returns a string representation of this appointment.
     * 
     * @return Formatted string with all appointment details
     */
    @Override
    public String toString() {
        return "\n\nAppointment" +
                "\nDate & Time : " + DateUtil.format(datetime) +
                "\nStatus : '" + status + '\'' +
                "\nDoctor : " + doctor.getName() +
                "\nPatient : " + patient.getName();
    }
}
