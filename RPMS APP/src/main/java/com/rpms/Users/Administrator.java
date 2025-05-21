package com.rpms.Users;

import java.util.ArrayList;

import com.rpms.utilities.DataManager;

/**
 * Administrator class representing a system administrator in the healthcare system.
 * Extends the User class with administrator-specific functionality.
 * Contains static lists of all users in the system.
 */
public class Administrator extends User {
    /** Static list of all doctors in the system */
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    
    /** Static list of all patients in the system */
    private static ArrayList<Patient> patients = new ArrayList<>();
    
    /** Static list of all administrators in the system */
    private static ArrayList<Administrator> admins = new ArrayList<>();
    
    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /** Static list of system logs */
    private static ArrayList<String> systemLogs = new ArrayList<>();

    /**
     * Constructor to initialize a new Administrator with all required fields
     * 
     * @param id Unique identifier for the administrator
     * @param name Full name of the administrator
     * @param phoneNumber Contact phone number
     * @param email Email address
     * @param username Username for login
     * @param password Password for authentication
     */
    public Administrator(String id, String name, String phoneNumber, String email, String username, String password) {
        super(id, name, phoneNumber, email, username, password);
    }

    /**
     * Returns the role of this user
     * @return "Administrator" as the role
     */
    @Override
    public String getRole() {
        return "Administrator";
    }

    /**
     * Gets the administrator's username
     * 
     * @return The username
     */
    @Override
    public String getUsername() {
        return super.getUsername();
    }

    //------------------------------
    // Static Getters
    //------------------------------
    
    /**
     * Gets the list of all doctors in the system
     * @return ArrayList of all doctors
     */
    public static ArrayList<Doctor> getDoctors() { return doctors; }
    
    /**
     * Gets the list of all patients in the system
     * @return ArrayList of all patients
     */
    public static ArrayList<Patient> getPatients() { return patients; }
    
    /**
     * Gets the list of all administrators in the system
     * @return ArrayList of all administrators
     */
    public static ArrayList<Administrator> getAdministrators() { return admins; }
    
    /**
     * Gets a list of all users in the system (doctors, patients, and administrators)
     * @return ArrayList of all users
     */
    public static ArrayList<User> getAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.addAll(doctors);
        allUsers.addAll(patients);
        allUsers.addAll(admins);
        return allUsers;
    }
    
    /**
     * Gets the list of all system logs
     * @return ArrayList of system log entries
     */
    public static ArrayList<String> getSystemLogs() { return systemLogs; }

    //------------------------------
    // Log Management Methods
    //------------------------------
    
    /**
     * Adds a new log entry to the system logs
     * @param log Log entry text
     */
    public static void addSystemLog(String log) {
        systemLogs.add(log);
    }
    
    /**
     * Clears all system logs
     */
    public static void clearSystemLogs() {
        systemLogs.clear();
        System.out.println("System logs cleared.");
        // Adding the log to the system logs
        systemLogs.add("System logs cleared.");
    }

    //------------------------------
    // User Registration Methods
    //------------------------------
    
    /**
     * Registers a new doctor in the system
     * @param doctor Doctor to register
     */
    public static void registerDoctor(Doctor doctor) {
        doctors.add(doctor);
        // Adding the doctor to the system logs
        systemLogs.add("Doctor " + doctor.getName() + " registered.");
        System.out.println("Doctor " + doctor.getName() + " registered.");
        DataManager.saveAllData(); // Auto-save
    }
    
    /**
     * Registers a new patient in the system
     * @param patient Patient to register
     */
    public static void registerPatient(Patient patient) {
        patients.add(patient);
        // Adding the patient to the system logs
        systemLogs.add("Patient " + patient.getName() + " registered.");
        System.out.println("Patient " + patient.getName() + " registered.");
        DataManager.saveAllData(); // Auto-save
    }
    
    /**
     * Registers a new administrator in the system
     * @param admin Administrator to register
     */
    public static void registerAdministrator(Administrator admin) {
        admins.add(admin);
        System.out.println("Admin " + admin.getName() + " added to the system.");
        // Adding the admin to the system logs
        systemLogs.add("Admin " + admin.getName() + " added to the system.");
        DataManager.saveAllData(); // Auto-save
    }

    //------------------------------
    // User Removal Methods
    //------------------------------
    
    /**
     * Removes a doctor from the system
     * @param doctor Doctor to remove
     */
    public static void removeDoctor(Doctor doctor) {
        if (doctors.remove(doctor)) {
            System.out.println("Doctor " + doctor.getName() + " removed from the system.");
            // Adding the doctor to the system logs
            systemLogs.add("Doctor " + doctor.getName() + " removed from the system.");
            DataManager.saveAllData(); // Auto-save
        } else {
            System.out.println("Doctor not found.");
        }
    }
    
    /**
     * Removes a patient from the system
     * @param patient Patient to remove
     */
    public static void removePatient(Patient patient) {
        if (patients.remove(patient)) {
            System.out.println("Patient " + patient.getName() + " removed from the system.");
            // Adding the patient to the system logs
            systemLogs.add("Patient " + patient.getName() + " removed from the system.");
            DataManager.saveAllData(); // Auto-save
        } else {
            System.out.println("Patient not found.");
        }
    }
    
    /**
     * Removes an administrator from the system
     * @param admin Administrator to remove
     */
    public static void removeAdministrator(Administrator admin) {
        if (admins.remove(admin)) {
            System.out.println("Admin " + admin.getName() + " removed from the system.");
            // Adding the admin to the system logs
            systemLogs.add("Admin " + admin.getName() + " removed from the system.");
            DataManager.saveAllData(); // Auto-save
        } else {
            System.out.println("Admin not found.");
        }
    }
    
    public static void clearPatients() {
    patients.clear();
}

public static void clearDoctors() {
    doctors.clear();
}

public static void clearAdministrators() {
    admins.clear();
}
    /**
     * Clear all users from the system (for testing/debugging purposes)
     */
    public static void clearAllUsers() {
        patients.clear();
        doctors.clear();
        admins.clear();
        System.out.println("All users cleared from system");
    }
}
