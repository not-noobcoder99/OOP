package com.rpms.NotificationsAndReminders;

/**
 * Interface defining the behavior for all notification types in the system.
 * Any class that can send notifications must implement this interface.
 */
public interface Notifiable {
    /**
     * Method to send a notification.
     * Implementing classes will define specific notification channels (SMS, email, etc.).
     */
    void sendNotification();
}
