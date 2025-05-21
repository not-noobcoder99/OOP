package com.rpms.HealthData;

import com.rpms.utilities.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a single vital sign record for a patient.
 * Contains measurements of various health indicators along with the timestamp when they were recorded.
 * All vital signs are validated to ensure they fall within medically acceptable ranges.
 */
public class VitalSign implements Serializable {
    // Basic vital sign measurements
    /** Patient ID this vital sign belongs to */
    private final String patientID;
    
    /** Heart rate in beats per minute (bpm) */
    private double heartRate;
    
    /** Blood oxygen saturation level (percentage) */
    private double oxygenLevel;
    
    /** Blood pressure in systolic/diastolic format (e.g., "120/80") */
    private String bloodPressure;
    
    /** Body temperature in Celsius */
    private double temperature;
    
    /** Date and time when the vital sign was recorded */
    private LocalDateTime dateTimeRecorded;

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to initialize a vital sign with all parameters.
     * Includes validation for each measurement.
     * 
     * @param patientID ID of the patient this vital sign belongs to
     * @param heartRate Heart rate in beats per minute
     * @param oxygenLevel Blood oxygen saturation level (0-100%)
     * @param bloodPressure Blood pressure in systolic/diastolic format
     * @param temperature Body temperature in Celsius
     * @param dateTimeRecorded Date and time when these measurements were taken
     */
    public VitalSign(String patientID, double heartRate, double oxygenLevel, String bloodPressure, double temperature, LocalDateTime dateTimeRecorded) {
        this.patientID = patientID;
        setHeartRate(heartRate);
        setOxygenLevel(oxygenLevel);
        setBloodPressure(bloodPressure);
        setTemperature(temperature);
        setDateRecorded(dateTimeRecorded);
    }

    /**
     * Alternative constructor for when patient ID is not specified.
     * Used when uploading vitals directly from CSV files.
     * 
     * @param heartRate Heart rate in beats per minute
     * @param oxygenLevel Blood oxygen saturation level (0-100%)
     * @param bloodPressure Blood pressure in systolic/diastolic format
     * @param temperature Body temperature in Celsius
     * @param dateTimeRecorded Date and time when these measurements were taken
     */
    public VitalSign(double heartRate, double oxygenLevel, String bloodPressure, double temperature, LocalDateTime dateTimeRecorded) {
        this("n/a", heartRate, oxygenLevel, bloodPressure, temperature, dateTimeRecorded);
    }

    // ===== Getters =====
    
    /**
     * Gets the ID of the patient this vital sign belongs to
     * @return Patient ID string
     */
    public String getUserId() { return patientID; }
    
    /**
     * Gets the heart rate measurement
     * @return Heart rate in beats per minute
     */
    public double getHeartRate() { return heartRate; }
    
    /**
     * Gets the blood oxygen saturation level
     * @return Oxygen level as a percentage (0-100%)
     */
    public double getOxygenLevel() { return oxygenLevel; }
    
    /**
     * Gets the blood pressure measurement
     * @return Blood pressure in systolic/diastolic format (e.g., "120/80")
     */
    public String getBloodPressure() { return bloodPressure; }
    
    /**
     * Gets the body temperature
     * @return Temperature in degrees Celsius
     */
    public double getTemperature() { return temperature; }
    
    /**
     * Gets the date and time when vital signs were recorded
     * @return LocalDateTime object representing the recording time
     */
    public LocalDateTime getDateTimeRecorded() { return dateTimeRecorded; }

    // ===== Setters with Validation =====
    
    /**
     * Sets the heart rate with validation
     * @param heartRate Heart rate in beats per minute (must be between 30-200)
     * @throws IllegalArgumentException if value is outside valid range
     */
    public void setHeartRate(double heartRate) {
        if (heartRate < 30 || heartRate > 200) {
            throw new IllegalArgumentException("Invalid heart rate. It must be between 30 and 200 bpm.");
        }
        this.heartRate = heartRate;
    }

    /**
     * Sets the blood oxygen level with validation
     * @param oxygenLevel Oxygen saturation percentage (must be between 0-100)
     * @throws IllegalArgumentException if value is outside valid range
     */
    public void setOxygenLevel(double oxygenLevel) {
        if (oxygenLevel < 0 || oxygenLevel > 100) {
            throw new IllegalArgumentException("Invalid oxygen level. It must be between 0 and 100 percent.");
        }
        this.oxygenLevel = oxygenLevel;
    }

    /**
     * Sets the blood pressure with validation
     * @param bloodPressure Blood pressure in systolic/diastolic format (e.g., "120/80")
     * @throws IllegalArgumentException if format or values are invalid
     */
    public void setBloodPressure(String bloodPressure) {
        String[] bpParts = bloodPressure.split("/");
        if (bpParts.length != 2) {
            throw new IllegalArgumentException("Invalid blood pressure format. It must be in the form 'systolic/diastolic'.");
        }

        try {
            int systolic = Integer.parseInt(bpParts[0]);
            int diastolic = Integer.parseInt(bpParts[1]);

            if (systolic < 60 || systolic > 250 || diastolic < 30 || diastolic > 150) {
                throw new IllegalArgumentException("Invalid blood pressure. Systolic must be between 60 and 250, and diastolic must be between 30 and 150.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid blood pressure values. Please provide numeric values.");
        }

        this.bloodPressure = bloodPressure;
    }

    /**
     * Sets the body temperature with validation
     * @param temperature Temperature in degrees Celsius (must be between 35.0-42.0)
     * @throws IllegalArgumentException if value is outside valid range
     */
    public void setTemperature(double temperature) {
        if (temperature < 35.0 || temperature > 42.0) {
            throw new IllegalArgumentException("Invalid temperature. It must be between 35.0 and 42.0 degrees Celsius.");
        }
        this.temperature = temperature;
    }

    /**
     * Sets the date and time when vital signs were recorded
     * @param dateTimeRecorded LocalDateTime representing the recording time
     * @throws IllegalArgumentException if date is null
     */
    public void setDateRecorded(LocalDateTime dateTimeRecorded) {
        if (dateTimeRecorded == null) {
            throw new IllegalArgumentException("Date and time recorded cannot be null.");
        }
        this.dateTimeRecorded = dateTimeRecorded;
    }

    /**
     * Generates a formatted string representation of this vital sign
     * @return Formatted string with all vital sign measurements
     */
    @Override
    public String toString() {
        return "HR: " + heartRate +
                "\nO2: " + oxygenLevel +
                "%\nTemp: " + temperature + "°C" +
                "\nDate & Time: " + DateUtil.format(dateTimeRecorded) + "\n\n";
    }
}
