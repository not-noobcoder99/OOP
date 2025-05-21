package com.rpms.utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.rpms.Users.User;

/**
 * Manages chat functionalities including history storage and deletion
 */
public class ChatManager {
    
    private static Map<String, Object> activeChatSessions = new HashMap<>();
    private static final String CHAT_HISTORY_DIR = "data/chats/";
    
    /**
     * Deletes chat history for a specific user
     * 
     * @param user The user whose chat history should be deleted
     * @return true if successful, false otherwise
     */
    public static boolean clearChatHistoryForUser(User user) {
        try {
            String userId = user.getId();
            
            // Log the action
            System.out.println("Clearing chat history for user: " + user.getName());
            
            // Clear file-based chat history if it exists
            String userChatDir = CHAT_HISTORY_DIR + userId + "/";
            deleteDirectory(new File(userChatDir));
            
            // Clear in-memory chat data
            if (activeChatSessions.containsKey(userId)) {
                activeChatSessions.remove(userId);
            }
            
            // Clear from DataManager
            return DataManager.clearChatHistoriesForUser(userId);
            
        } catch (Exception e) {
            System.err.println("Error clearing chat history: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Clears all chat histories in the system (admin function)
     * 
     * @return true if successful, false otherwise
     */
    public static boolean clearAllChatHistories() {
        try {
            // Log the action
            System.out.println("Clearing all chat histories");
            
            // Delete all chat files
            deleteDirectory(new File(CHAT_HISTORY_DIR));
            
            // Create the directory again for future use
            new File(CHAT_HISTORY_DIR).mkdirs();
            
            // Clear in-memory chat data
            activeChatSessions.clear();
            
            // Clear from DataManager
            return DataManager.clearAllChatHistories();
            
        } catch (Exception e) {
            System.err.println("Error clearing all chat histories: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to recursively delete a directory
     * 
     * @param directory The directory to delete
     * @return true if successful, false otherwise
     */
    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            return directory.delete();
        }
        return false;
    }
    
    /**
     * Ensures the chat directories exist
     */
    public static void initialize() {
        try {
            File chatDir = new File(CHAT_HISTORY_DIR);
            if (!chatDir.exists()) {
                chatDir.mkdirs();
                System.out.println("Created chat history directory");
            }
        } catch (Exception e) {
            System.err.println("Error initializing chat directories: " + e.getMessage());
        }
    }
}
