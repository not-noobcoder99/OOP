package com.rpms.ChatVideoConsultation;

import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.Users.User;
import com.rpms.utilities.DataManager;

import java.io.*;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central manager for the real-time chat system.
 * Implements a TCP socket-based chat server that handles multiple concurrent client connections,
 * message routing, and integration with the persistent data storage.
 */
public class ChatManager {
    /** Base port to try first when starting server */
    private static final int BASE_PORT = 12345;
    
    /** Current port the server is running on */
    private static int currentPort = BASE_PORT;
    
    /** The server socket accepting client connections */
    private static ServerSocket serverSocket;
    
    /** Flag indicating if the server is currently running */
    private static boolean isRunning = false;
    
    /** Map of connected users: user ID → ClientHandler */
    private static final Map<String, ClientHandler> connectedUsers = new ConcurrentHashMap<>();
    
    /** Thread pool for handling multiple client connections concurrently */
    private static ExecutorService threadPool;
    
    /**
     * Starts the chat server on an available port.
     * Tries multiple ports if the default port is already in use.
     * Creates a thread pool for handling client connections.
     */
    public static void startServer() {
        if (isRunning) return;
        
        // Try up to 10 ports
        for (int portOffset = 0; portOffset < 10; portOffset++) {
            currentPort = BASE_PORT + portOffset;
            try {
                serverSocket = new ServerSocket(currentPort);
                isRunning = true;
                threadPool = Executors.newCachedThreadPool();
                
                // Start a thread to listen for incoming connections
                new Thread(() -> {
                    try {
                        System.out.println("Chat server started on port " + currentPort);
                        while (isRunning) {
                            try {
                                Socket clientSocket = serverSocket.accept();
                                ClientHandler handler = new ClientHandler(clientSocket);
                                threadPool.execute(handler);
                            } catch (IOException e) {
                                if (isRunning) {
                                    System.err.println("Error accepting client connection: " + e.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (isRunning) {
                            System.err.println("Server thread error: " + e.getMessage());
                        }
                    }
                }).start();
                
                // Successfully started server
                return;
                
            } catch (BindException e) {
                // Port is in use, try next one
                System.out.println("Port " + currentPort + " is in use, trying next port...");
            } catch (IOException e) {
                System.err.println("Could not start chat server on port " + currentPort + ": " + e.getMessage());
            }
        }
        
        // If we got here, all ports were in use
        System.err.println("Failed to start chat server after trying multiple ports");
    }
    
    /**
     * Returns the current port the server is running on.
     * Used by clients to connect to the server.
     * 
     * @return The current server port
     */
    public static int getCurrentPort() {
        return currentPort;
    }
    
    /**
     * Stops the chat server and releases all resources.
     * Closes all client connections and shuts down the thread pool.
     */
    public static void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            if (threadPool != null) {
                threadPool.shutdown();
            }
            
            // Close all client connections
            for (ClientHandler handler : connectedUsers.values()) {
                handler.closeConnection();
            }
            connectedUsers.clear();
            
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }
    
    /**
     * Forces the release of the server port if it's in use.
     * Attempts to connect and immediately close to trigger port release.
     * This is useful when the server didn't shut down properly previously.
     */
    public static void forceReleasePort() {
        try {
            Socket socket = new Socket("localhost", currentPort);
            socket.close();
            System.out.println("Successfully connected to port " + currentPort + ", it appears to be in use.");
        } catch (ConnectException e) {
            // Port is not in use, which is good
            System.out.println("Port " + currentPort + " is available.");
        } catch (IOException e) {
            System.out.println("Error checking port " + currentPort + ": " + e.getMessage());
        }
    }
    
    /**
     * Inner class that handles individual client connections.
     * Each connected client gets its own ClientHandler instance.
     */
    private static class ClientHandler implements Runnable {
        /** The socket connection to this client */
        private Socket clientSocket;
        
        /** Stream for receiving objects from the client */
        private ObjectInputStream in;
        
        /** Stream for sending objects to the client */
        private ObjectOutputStream out;
        
        /** ID of the user this handler is serving */
        private String userId;
        
        /** Flag indicating if this handler is running */
        private boolean running = true;
        
        /**
         * Creates a new client handler for the specified socket.
         * 
         * @param socket Client's socket connection
         */
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        /**
         * Closes the connection to the client.
         * Called when the server is shutting down or the client disconnects.
         */
        public void closeConnection() {
            running = false;
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }
        
        /**
         * Main processing loop for this client connection.
         * Sets up streams, authenticates the user, and processes messages.
         */
        @Override
        public void run() {
            try {
                // Create streams - ORDER IS IMPORTANT to avoid deadlock
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush(); // Important to avoid deadlock
                in = new ObjectInputStream(clientSocket.getInputStream());
                
                // First message should be the user ID
                Object userIdObj = in.readObject();
                if (userIdObj instanceof String) {
                    userId = (String) userIdObj;
                    connectedUsers.put(userId, this);
                    
                    System.out.println("User " + userId + " connected to chat server");
                    
                    // Process incoming messages
                    while (running && isRunning && !clientSocket.isClosed()) {
                        try {
                            Object obj = in.readObject();
                            if (obj instanceof ChatMessage) {
                                ChatMessage message = (ChatMessage) obj;
                                processMessage(message);
                            }
                        } catch (EOFException | SocketException e) {
                            // Client disconnected
                            break;
                        } catch (ClassNotFoundException e) {
                            System.err.println("Unknown message type received: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("I/O Error with client " + userId + ": " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Error reading object from client: " + e.getMessage());
            } finally {
                if (userId != null) {
                    connectedUsers.remove(userId);
                    System.out.println("User " + userId + " disconnected from chat server");
                }
                
                // Close resources
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client resources: " + e.getMessage());
                }
            }
        }
        
        /**
         * Processes a chat message received from the client.
         * Saves the message to persistent storage and routes it to the recipient if online.
         * 
         * @param message The chat message to process
         */
        private void processMessage(ChatMessage message) {
            try {
                // CRITICAL: Save the message to persistent storage FIRST
                DataManager.addChatMessage(message);
                System.out.println("Message saved: from " + message.getSenderId() + " to " + message.getReceiverId());
                
                // Send to recipient if online
                String recipientId = message.getReceiverId();
                ClientHandler recipient = connectedUsers.get(recipientId);
                
                if (recipient != null) {
                    recipient.sendMessage(message);
                }
                
                // Confirm receipt to sender
                out.writeObject("DELIVERED");
                out.flush();
                out.reset(); // Reset object cache to avoid memory leaks
            } catch (IOException e) {
                System.err.println("Error sending delivery confirmation: " + e.getMessage());
            }
        }
        
        /**
         * Sends a message to this client.
         * 
         * @param message The message to send
         */
        public void sendMessage(ChatMessage message) {
            try {
                out.writeObject(message);
                out.flush();
                out.reset(); // Reset object cache to avoid memory leaks
            } catch (IOException e) {
                System.err.println("Error sending message to recipient: " + e.getMessage());
                closeConnection(); // Close this connection if it's broken
            }
        }
    }
    
    /**
     * Gets a user's name by their ID.
     * Searches through all registered users in the system.
     * 
     * @param userId ID of the user to look up
     * @return User's name, or "Unknown User" if not found
     */
    public static String getUserNameById(String userId) {
        for (User user : Administrator.getAllUsers()) {
            if (user.getId().equals(userId)) {
                return user.getName();
            }
        }
        return "Unknown User";
    }
    
    /**
     * Gets all users that a specific user can chat with.
     * For patients: their assigned doctor and any doctors who have them as patients.
     * For doctors: all their assigned patients.
     * 
     * @param user The user to find chat contacts for
     * @return List of users that can be contacted
     */
    public static List<User> getChatContactsForUser(User user) {
        List<User> contacts = new ArrayList<>();
        
        if (user instanceof Patient patient) {
            // 1. Add their physician
            Doctor physician = patient.getPhysician();
            if (physician != null) {
                contacts.add(physician);
            }
            
            // 2. Add all doctors who have this patient in their list
            for (Doctor doctor : Administrator.getDoctors()) {
                if (doctor.getPatients().contains(patient) && !contacts.contains(doctor)) {
                    contacts.add(doctor);
                }
            }
        } else if (user instanceof Doctor doctor) {
            // Doctors can chat with all their patients
            contacts.addAll(doctor.getPatients());
        }
        
        return contacts;
    }
}