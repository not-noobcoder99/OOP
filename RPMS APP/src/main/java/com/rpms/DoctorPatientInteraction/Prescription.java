package com.rpms.DoctorPatientInteraction;

import java.io.Serializable;

/**
 * Represents a medication prescription given by a doctor to a patient.
 * Contains information about the medication, dosage, and administration schedule.
 */
public class Prescription implements Serializable {
    // required attributes for prescription
    /** Name of the prescribed medication */
    private String medication;
    
    /** Medication dosage (e.g., "500mg") */
    private String dosage;
    
    /** Administration schedule (e.g., "Twice daily with meals") */
    private String schedule;
    
    /** Additional notes or special instructions */
    private String notes;

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to create a prescription with medication details.
     * 
     * @param medication Name of the medication
     * @param dosage Amount and unit (e.g., "500mg")
     * @param schedule When to take the medication (e.g., "Twice daily")
     */
    public Prescription(String medication, String dosage, String schedule) {
        this.medication = medication;
        this.dosage = dosage;
        this.schedule = schedule;
    }
    
    /**
     * Alternative constructor that includes additional notes.
     * 
     * @param medication Name of the medication
     * @param dosage Amount and unit (e.g., "500mg")
     * @param schedule When to take the medication (e.g., "Twice daily")
     * @param notes Special instructions or warnings
     */
    public Prescription(String medication, String dosage, String schedule, String notes) {
        this.medication = medication;
        this.dosage = dosage;
        this.schedule = schedule;
        this.notes = notes;
    }
    
    // getters
    /**
     * Gets the medication name
     * @return Name of medication
     */
    public String getMedication() { return medication; }
    
    /**
     * Gets the prescribed dosage
     * @return Dosage as a string
     */
    public String getDosage() { return dosage; }
    
    /**
     * Gets the administration schedule
     * @return Schedule as a string
     */
    public String getSchedule() { return schedule; }
    
    /**
     * Gets additional notes
     * @return Notes as a string
     */
    public String getNotes() { return notes; }

    // setters
    /**
     * Sets the medication name
     * @param medication New medication name
     */
    public void setMedication(String medication) { this.medication = medication; }
    
    /**
     * Sets the dosage
     * @param dosage New dosage information
     */
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    /**
     * Sets the administration schedule
     * @param schedule New schedule information
     */
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    /**
     * Sets additional notes
     * @param notes New notes
     */
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Creates a string representation of this prescription
     * @return Formatted string with medication details
     */
    @Override
    public String toString() {
        String result = "Medication: " + medication + ", Dosage: " + dosage + ", Schedule: " + schedule;
        if (notes != null && !notes.isEmpty()) {
            result += ", Notes: " + notes;
        }
        return result;
    }
}
