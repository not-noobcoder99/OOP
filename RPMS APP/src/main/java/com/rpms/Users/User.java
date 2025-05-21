package com.rpms.Users;

import java.io.Serializable;

/**
 * Base abstract class for all users in the system.
 * Provides common attributes and behaviors shared by Doctors, Patients and Administrators.
 * Implements Serializable to allow for object persistence.
 */
public abstract class User implements Serializable {
    // Basic data fields that all users possess
    /** Unique identifier for the user */
    protected final String id;
    
    /** Full name of the user */
    protected String name;
    
    /** Contact phone number */
    protected String phoneNumber;
    
    /** Email address */
    protected String email;
    
    /** Username for login purposes */
    protected String username;
    
    /** Password for authentication */
    protected String password;

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to initialize a user with all required fields
     * 
     * @param id Unique identifier for the user
     * @param name Full name of the user
     * @param phoneNumber Contact phone number
     * @param email Email address
     * @param username Username for login
     * @param password Password for authentication
     */
    public User(String id, String name, String phoneNumber, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
        // Use setter for validation
        setPassword(password);
    }
    
    //------------------------------
    // Getters and Setters
    //------------------------------

    /**
     * @return The user's unique ID
     */
    public String getId() { return id; }
    
    /**
     * @return The user's full name
     */
    public String getName() { return name; }
    
    /**
     * @return The user's email address
     */
    public String getEmail() { return email; }
    
    /**
     * @return The user's phone number
     */
    public String getPhoneNumber() { return phoneNumber; }
    
    /**
     * Gets the username of the user
     * 
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's password
     * @param password The new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Sets the user's name
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the user's phone number
     * @param phoneNumber The new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the user's email address
     * @param email The new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    //------------------------------
    // Authentication Methods
    //------------------------------
    
    /**
     * Verifies if the provided password matches the user's password
     * @param inputPassword Password to check
     * @return true if passwords match, false otherwise
     */
    public boolean checkPassword(String inputPassword) {
        boolean match = this.password.equals(inputPassword);
        if (!match) {
            System.out.println("Password mismatch for user: " + this.name + 
                      " (Expected: " + this.password + ", Got: " + inputPassword + ")");
        }
        return match;
    }
    
    /**
     * Verifies if the provided username matches the user's username
     * @param inputUsername Username to check
     * @return true if usernames match, false otherwise
     */
    public boolean checkUsername(String inputUsername) {
        boolean match = this.username.equals(inputUsername);
        if (!match) {
            System.out.println("Username mismatch for user: " + this.name + 
                      " (Expected: " + this.username + ", Got: " + inputUsername + ")");
        }
        return match;
    }
    
    /**
     * Returns the role of the user (to be implemented by subclasses)
     * @return String representation of the user's role
     */
    public abstract String getRole();
    
    //------------------------------
    // Object Overrides
    //------------------------------
    
    /**
     * Returns a string representation of the user
     * @return String with user ID, name and email
     */
    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Email: " + email;
    }
    
    /**
     * Checks if this user equals another user based on ID
     * @param obj The object to compare with
     * @return true if IDs match, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        // Users are equal if they have the same ID
        return this.id.equals(user.id);
    }
}
