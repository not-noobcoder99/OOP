package com.rpms.utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rpms.AppointmentHandling.Appointment;
import com.rpms.AppointmentHandling.AppointmentManager;
import com.rpms.ChatVideoConsultation.ChatHistory;
import com.rpms.ChatVideoConsultation.ChatMessage;
import com.rpms.ChatVideoConsultation.VideoCall;
import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;

/**
 * Manages automatic data persistence operations
 */
public class DataManager {

    private static final String DATA_DIR = "data";
    private static final String DOCTORS_FILE = DATA_DIR + "/doctors.ser";
    private static final String PATIENTS_FILE = DATA_DIR + "/patients.ser";
    private static final String ADMINS_FILE = DATA_DIR + "/admins.ser";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "/appointments.ser";
    private static final String VIDEOCALLS_FILE = DATA_DIR + "/videocalls.ser";
    private static final String CHAT_HISTORIES_FILE = DATA_DIR + "/chat_histories.ser";
    private static final String LOGS_FILE = DATA_DIR + "/logs.ser";

    private static ArrayList<ChatHistory> chatHistories = new ArrayList<>();

    /**
     * Initializes the data directory
     */
    private static void initDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
            System.out.println("Created data directory: " + dataDir.getAbsolutePath());
        }
    }

    /**
     * Saves all system data automatically
     */
    public static void saveAllData() {
        initDataDirectory();

        // Save doctors
        SerializationUtil.serializeObject(Administrator.getDoctors(), DOCTORS_FILE);

        // Save patients
        SerializationUtil.serializeObject(Administrator.getPatients(), PATIENTS_FILE);

        // Save administrators
        SerializationUtil.serializeObject(Administrator.getAdministrators(), ADMINS_FILE);

        // Save appointments
        SerializationUtil.serializeObject(AppointmentManager.getAppointments(), APPOINTMENTS_FILE);

        // Save video calls
        SerializationUtil.serializeObject(AppointmentManager.getVideoCalls(), VIDEOCALLS_FILE);

        // Save chat histories
        SerializationUtil.serializeObject(chatHistories, CHAT_HISTORIES_FILE);

        // Save system logs
        SerializationUtil.serializeObject(Administrator.getSystemLogs(), LOGS_FILE);

        // Log the save operation
        Administrator.addSystemLog("System data automatically saved at " + java.time.LocalDateTime.now());
    }

    /**
     * Loads all system data automatically
     */
    public static void loadAllData() {
        try {
            initDataDirectory();

            // Load doctors
            ArrayList<Doctor> doctors = SerializationUtil.deserializeObject(DOCTORS_FILE);
            if (doctors != null && !doctors.isEmpty()) {
                Administrator.getDoctors().clear();
                Administrator.getDoctors().addAll(doctors);
                System.out.println("Loaded " + doctors.size() + " doctors");
            } else {
                System.out.println("No doctors loaded - file doesn't exist or is empty");
            }

            // Load patients
            ArrayList<Patient> patients = SerializationUtil.deserializeObject(PATIENTS_FILE);
            if (patients != null && !patients.isEmpty()) {
                Administrator.getPatients().clear();
                Administrator.getPatients().addAll(patients);
                System.out.println("Loaded " + patients.size() + " patients");
            } else {
                System.out.println("No patients loaded - file doesn't exist or is empty");
            }

            // Load administrators
            ArrayList<Administrator> admins = SerializationUtil.deserializeObject(ADMINS_FILE);
            if (admins != null && !admins.isEmpty()) {
                Administrator.getAdministrators().clear();
                Administrator.getAdministrators().addAll(admins);
                System.out.println("Loaded " + admins.size() + " administrators");
            } else {
                System.out.println("No administrators loaded - file doesn't exist or is empty");
            }

            // Load appointments
            ArrayList<Appointment> appointments = SerializationUtil.deserializeObject(APPOINTMENTS_FILE);
            if (appointments != null && !appointments.isEmpty()) {
                AppointmentManager.getAppointments().clear();
                AppointmentManager.getAppointments().addAll(appointments);
                System.out.println("Loaded " + appointments.size() + " appointments");
            }

            // Load video calls
            ArrayList<VideoCall> videoCalls = SerializationUtil.deserializeObject(VIDEOCALLS_FILE);
            if (videoCalls != null && !videoCalls.isEmpty()) {
                AppointmentManager.getVideoCalls().clear();
                AppointmentManager.getVideoCalls().addAll(videoCalls);
                System.out.println("Loaded " + videoCalls.size() + " video calls");
            }

            // Load chat histories
            ArrayList<ChatHistory> chats = SerializationUtil.deserializeObject(CHAT_HISTORIES_FILE);
            if (chats != null && !chats.isEmpty()) {
                chatHistories.clear();
                chatHistories.addAll(chats);
                System.out.println("Loaded " + chats.size() + " chat histories");
            }

            // Load system logs
            ArrayList<String> logs = SerializationUtil.deserializeObject(LOGS_FILE);
            if (logs != null && !logs.isEmpty()) {
                Administrator.getSystemLogs().clear();
                Administrator.getSystemLogs().addAll(logs);
                System.out.println("Loaded " + logs.size() + " system logs");
            }

            // Log the load operation
            Administrator.addSystemLog("System data automatically loaded at " + java.time.LocalDateTime.now());
            
            System.out.println("Total users after loading: " + Administrator.getAllUsers().size());
        } catch (Exception e) {
            System.err.println("Error during data loading: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Automatically saves a specific doctor's data
     */
    public static void saveDoctor(Doctor doctor) {
        initDataDirectory();
        String filename = DATA_DIR + "/doctor_" + doctor.getId() + ".ser";
        SerializationUtil.serializeObject(doctor, filename);
    }

    /**
     * Automatically saves a specific patient's data
     */
    public static void savePatient(Patient patient) {
        initDataDirectory();
        String filename = DATA_DIR + "/patient_" + patient.getId() + ".ser";
        SerializationUtil.serializeObject(patient, filename);
    }

    /**
     * Gets the chat history between two users
     */
    public static ChatHistory getChatHistory(String user1Id, String user2Id) {
        for (ChatHistory history : chatHistories) {
            if (history.isForUsers(user1Id, user2Id)) {
                return history;
            }
        }
        // If no chat history exists, create a new one
        ChatHistory newHistory = new ChatHistory(user1Id, user2Id);
        chatHistories.add(newHistory);
        
        // IMPORTANT: Save when creating a new chat history
        saveAllData();
        
        return newHistory;
    }

    /**
     * Adds a new message to chat history
     */
    public static synchronized void addChatMessage(ChatMessage message) {
        ChatHistory history = getChatHistory(message.getSenderId(), message.getReceiverId());
        history.addMessage(message);
        
        // IMPORTANT: Make sure to save immediately after adding a message
        saveAllData();
        
        System.out.println("Added message to chat history: from " + message.getSenderId() + 
                          " to " + message.getReceiverId() + 
                          " - Content: " + message.getContent());
    }
    
    /**
     * Clears all chat histories for a specific user
     * 
     * @param userId The ID of the user whose chat histories should be cleared
     * @return true if successful, false otherwise
     */
    public static boolean clearChatHistoriesForUser(String userId) {
        try {
            List<ChatHistory> toRemove = new ArrayList<>();
            
            // Find all chat histories involving this user
            for (ChatHistory history : chatHistories) {
                if (history.isForUsers(userId, history.getUser2Id()) || 
                    history.isForUsers(history.getUser1Id(), userId)) {
                    toRemove.add(history);
                }
            }
            
            // Remove the identified histories
            chatHistories.removeAll(toRemove);
            
            // Save changes
            saveAllData();
            
            Administrator.addSystemLog("Chat histories cleared for user ID: " + userId);
            return true;
        } catch (Exception e) {
            System.err.println("Error clearing chat histories for user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Clears all chat histories in the system
     * 
     * @return true if successful, false otherwise
     */
    public static boolean clearAllChatHistories() {
        try {
            chatHistories.clear();
            saveAllData();
            
            Administrator.addSystemLog("All chat histories in the system have been cleared");
            return true;
        } catch (Exception e) {
            System.err.println("Error clearing all chat histories: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}