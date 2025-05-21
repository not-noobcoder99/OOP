package com.rpms.ChatVideoConsultation;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.utilities.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a video consultation appointment between a doctor and patient.
 * Includes scheduling details, meeting link, and status tracking.
 */
public class VideoCall implements Serializable {
    /** The doctor participating in the video call */
    private Doctor doctor;
    
    /** The patient participating in the video call */
    private Patient patient;
    
    /** URL link to the video conference (e.g., Google Meet) */
    private String meetingLink;
    
    /** When the video call should begin */
    private LocalDateTime startTime;
    
    /** When the video call should end */
    private LocalDateTime endTime;
    
    /** Current status of the video call (Pending, Approved, Cancelled) */
    private String status;

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new video call with scheduling information.
     * The meeting link is initially "notprovided" until approved.
     * 
     * @param doctor The doctor conducting the consultation
     * @param patient The patient receiving the consultation
     * @param startTime When the video call should start
     * @param endTime When the video call should end
     */
    public VideoCall(Doctor doctor, Patient patient, LocalDateTime startTime, LocalDateTime endTime) {
        this.doctor = doctor;
        this.patient = patient;
        this.meetingLink = "notprovided";
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "Pending";
    }

    /**
     * Gets the doctor for this video call
     * @return Doctor object
     */
    public Doctor getDoctor() { return doctor; }
    
    /**
     * Sets the doctor for this video call
     * @param doctor New doctor
     */
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    /**
     * Gets the patient for this video call
     * @return Patient object
     */
    public Patient getPatient() { return patient; }
    
    /**
     * Sets the patient for this video call
     * @param patient New patient
     */
    public void setPatient(Patient patient) { this.patient = patient; }

    /**
     * Gets the meeting link
     * @return URL link as a string
     */
    public String getLink() { return meetingLink; }
    
    /**
     * Sets the meeting link
     * @param meetingLink New meeting URL
     */
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    /**
     * Gets the scheduled start time
     * @return Start time as LocalDateTime
     */
    public LocalDateTime getStartTime() { return startTime; }
    
    /**
     * Sets the scheduled start time
     * @param startTime New start time
     */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    /**
     * Gets the scheduled end time
     * @return End time as LocalDateTime
     */
    public LocalDateTime getEndTime() { return endTime; }
    
    /**
     * Sets the scheduled end time
     * @param endTime New end time
     */
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    /**
     * Gets the current status of the video call
     * @return Status string (Pending, Approved, or Cancelled)
     */
    public String getStatus() { return status; }
    
    /**
     * Sets the status of the video call.
     * Only accepts valid status values: "Pending", "Approved", or "Cancelled".
     * 
     * @param status New status value
     */
    public void setStatus(String status) {
        if (status.equals("Pending") || status.equals("Approved") || status.equals("Cancelled")) {
            this.status = status;
        } else {
            System.out.println("Invalid status. Status must be either 'Pending', 'In Progress', or 'Completed'.");
        }
    }

    /**
     * Generates meeting information for starting the video call.
     * 
     * @return Formatted string with call details and joining instructions
     */
    public String startCall() {
        return "Video Call between " + doctor.getName() + " and " + patient.getName() + "\nJoin here: " + meetingLink +
                "\nStart Time: " + startTime + "\nEnd Time: " + endTime;
    }

    /**
     * Creates a string representation of this video call
     * @return Formatted string with all video call details
     */
    @Override
    public String toString() {
        return "VideoCall" +
                "\nDoctor=" + doctor.getName() +
                "\nPatient=" + patient.getName() +
                "\nMeetingLink='" + meetingLink + '\'' +
                "\nstartTime=" + DateUtil.format(startTime) +
                "\nendTime=" + DateUtil.format(endTime) +
                "\nstatus='" + status + '\'';
    }

    /**
     * Sets the meeting link, typically when a doctor approves the call
     * @param link URL to the video meeting
     */
    public void setLink(String link) {
        this.meetingLink = link;
    }
}
