package com.rpms.DoctorPatientInteraction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.rpms.Users.Doctor;
import com.rpms.utilities.DateUtil;

/**
 * Represents medical feedback provided by a doctor to a patient.
 * Contains textual comments, a collection of prescriptions, and a timestamp.
 */
public class Feedback implements Serializable {
    // datafields
    /** Comments provided by the doctor */
    private String comments;
    
    /** Collection of prescribed medications */
    private ArrayList<Prescription> prescriptions; // ArrayList to hold prescriptions
    
    /** Date and time when feedback was given */
    private LocalDateTime datetime;// date of the feedback
    
    /** Doctor who provided the feedback */
    private Doctor doctor;

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to create a new feedback record with prescriptions.
     * 
     * @param comments Doctor's comments about the patient's condition
     * @param prescriptions List of medications prescribed to the patient
     * @param datetime Date and time when feedback was given
     */
    public Feedback(String comments, ArrayList<Prescription> prescriptions, LocalDateTime datetime) {
        this.datetime = datetime;
        this.comments = comments;
        // initializing the arraylist of prescriptions
        this.prescriptions = prescriptions;
    }

    // getters and setters
    /**
     * Gets the doctor's comments
     * @return Comments as a string
     */
    public String getComments() { return comments; }
    
    /**
     * Gets the date and time when feedback was given
     * @return LocalDateTime object
     */
    public LocalDateTime getDate() { return datetime; }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    /**
     * Gets the list of prescriptions
     * @return ArrayList of Prescription objects
     */
    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }

    // setters
    /**
     * Sets the doctor's comments
     * @param comments New comments text
     */
    public void setComments(String comments) { this.comments = comments; }
    
    /**
     * Sets the date and time of feedback
     * @param datetime New date and time
     */
    public void setDate(LocalDateTime datetime) { this.datetime = datetime; }

    /**
     * Adds a new prescription to this feedback
     * @param prescription Prescription to add
     */
    public void addPrescription(Prescription prescription) {
        this.prescriptions.add(prescription);
    }

    /**
     * Creates a string representation of this feedback
     * @return Formatted string with comments and timestamp
     */
    @Override
    public String toString() {
        return "Comments: " + comments +
                "\nDate & Time: " + DateUtil.format(datetime);
    }
}
