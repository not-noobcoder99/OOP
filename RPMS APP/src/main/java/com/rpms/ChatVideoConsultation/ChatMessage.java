package com.rpms.ChatVideoConsultation;

import com.rpms.utilities.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a single message exchanged between users in the chat system.
 * This class is serializable to allow transmission over networks and storage in files.
 */
public class ChatMessage implements Serializable {
    /** ID of the user who sent the message */
    private String senderId;
    
    /** Name of the user who sent the message */
    private String senderName;
    
    /** ID of the user who should receive the message */
    private String receiverId;
    
    /** The actual text content of the message */
    private String content;
    
    /** When the message was sent */
    private LocalDateTime timestamp;
    
    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new chat message with the specified details.
     * Automatically sets the timestamp to the current time.
     * 
     * @param senderId ID of the user sending the message
     * @param senderName Name of the user sending the message
     * @param receiverId ID of the user receiving the message
     * @param content The text content of the message
     */
    public ChatMessage(String senderId, String senderName, String receiverId, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Gets the ID of the sender
     * @return Sender's user ID
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Gets the name of the sender
     * @return Sender's name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Gets the ID of the receiver
     * @return Receiver's user ID
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * Gets the message content
     * @return Message text
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the timestamp when the message was created
     * @return Message creation time
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a formatted string representation of this message
     * @return String with timestamp, sender name and message content
     */
    @Override
    public String toString() {
        return "[" + DateUtil.format(timestamp) + "] " + senderName + ": " + content;
    }
}