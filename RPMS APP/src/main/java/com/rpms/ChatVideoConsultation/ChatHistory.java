package com.rpms.ChatVideoConsultation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains the history of chat messages between two specific users.
 * This class stores all messages exchanged in a conversation and provides
 * methods to add messages and retrieve the conversation history.
 */
public class ChatHistory implements Serializable {
    /** ID of the first user in this chat conversation */
    private String user1Id;
    
    /** ID of the second user in this chat conversation */
    private String user2Id;
    
    /** Collection of all messages exchanged between these users */
    private ArrayList<ChatMessage> messages;
    
    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new chat history between two specific users.
     * 
     * @param user1Id ID of the first user
     * @param user2Id ID of the second user
     */
    public ChatHistory(String user1Id, String user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.messages = new ArrayList<>();
    }

    /**
     * Adds a new message to this chat history.
     * 
     * @param message The chat message to add
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    /**
     * Gets all messages in this chat history.
     * 
     * @return List of all chat messages
     */
    public List<ChatMessage> getMessages() {
        return messages;
    }

    /**
     * Gets the ID of the first user.
     * 
     * @return First user's ID
     */
    public String getUser1Id() {
        return user1Id;
    }

    /**
     * Gets the ID of the second user.
     * 
     * @return Second user's ID
     */
    public String getUser2Id() {
        return user2Id;
    }

    /**
     * Checks if this chat history belongs to the specified pair of users.
     * Order of the users doesn't matter - the check works in both directions.
     * 
     * @param userId1 ID of the first user to check
     * @param userId2 ID of the second user to check
     * @return true if this history is for the specified users, false otherwise
     */
    public boolean isForUsers(String userId1, String userId2) {
        return (user1Id.equals(userId1) && user2Id.equals(userId2)) ||
               (user1Id.equals(userId2) && user2Id.equals(userId1));
    }
}