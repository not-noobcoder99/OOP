package com.rpms.GUI;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.rpms.ChatVideoConsultation.ChatHistory;
import com.rpms.ChatVideoConsultation.ChatManager;
import com.rpms.ChatVideoConsultation.ChatMessage;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.Users.User;
import com.rpms.utilities.DataManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Provides a modern chat interface for communication between doctors and patients.
 * Handles connection to the chat server, message sending/receiving, and chat history.
 */
public class ChatWindow {
    /** The current user (patient or doctor) using this chat window */
    private User currentUser;
    
    /** The user the current user is chatting with */
    private User chatPartner;
    
    /** The window stage for this chat */
    private Stage stage;
    
    /** ScrollPane to display the chat history and messages */
    private ScrollPane chatScrollPane;
    
    /** VBox to hold all chat messages */
    private VBox chatMessagesBox;
    
    /** Socket connection to the chat server */
    private Socket socket;
    
    /** Output stream to send messages to the server */
    private ObjectOutputStream out;
    
    /** Input stream to receive messages from the server */
    private ObjectInputStream in;
    
    /** Flag indicating if connected to the chat server */
    private boolean isConnected = false;
    
    /** Thread for receiving messages asynchronously */
    private Thread receiveThread;
    
    /** Text field for entering messages */
    private TextField messageField;
    
    /** Status indicator circle */
    private Circle statusIndicator;
    
    /** Status label */
    private Label statusLabel;
    
    /** Date formatter for message timestamps */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Creates a new chat window between two users.
     * 
     * @param currentUser The user who initiated the chat
     * @param chatPartner The user to chat with
     */
    public ChatWindow(User currentUser, User chatPartner) {
        this.currentUser = currentUser;
        this.chatPartner = chatPartner;
        this.stage = new Stage();
    }

    /**
     * Initializes and displays the chat window interface.
     * Sets up UI components, loads chat history, and connects to the chat server.
     */
    public void show() {
        stage.setTitle("Chat with " + chatPartner.getName());

        // Create root layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("chat-root");
        
        // Create header
        HBox header = createHeader();
        root.setTop(header);
        
        // Create chat message area
        chatMessagesBox = new VBox(10);
        chatMessagesBox.setPadding(new Insets(15));
        
        chatScrollPane = new ScrollPane(chatMessagesBox);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToHeight(true);
        chatScrollPane.getStyleClass().add("chat-scrollpane");
        
        root.setCenter(chatScrollPane);
        
        // Create input area
        HBox inputBox = createInputBox();
        root.setBottom(inputBox);
        
        // Create scene
        Scene scene = new Scene(root, 450, 600);
        String cssPath = com.rpms.Main.getStylesheetPath();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }
        
        stage.setScene(scene);
        stage.show();
        
        // Load chat history
        loadChatHistory();
        
        // Connect to chat server
        connectToServer();
        
        // Handle window close
        stage.setOnCloseRequest(e -> disconnect());
    }
    
    /**
     * Creates the chat header with user info and status
     * 
     * @return HBox containing the header elements
     */
    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("chat-header-box");
        
        // Create avatar circle
        Circle avatar = new Circle(20);
        avatar.setFill(chatPartner instanceof Doctor ? Color.CORNFLOWERBLUE : Color.LIGHTGREEN);
        
        // User info
        VBox userInfo = new VBox(3);
        Label nameLabel = new Label(chatPartner.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.WHITE);
        
        // Status indicator
        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        statusIndicator = new Circle(5);
        statusIndicator.setFill(Color.RED);
        
        statusLabel = new Label("Disconnected");
        statusLabel.setTextFill(Color.LIGHTGRAY);
        statusLabel.setFont(Font.font("System", 12));
        
        statusBox.getChildren().addAll(statusIndicator, statusLabel);
        userInfo.getChildren().addAll(nameLabel, statusBox);
        
        // Button to open video call link if available
        if (currentUser instanceof Patient && chatPartner instanceof Doctor) {
            Button videoCallButton = new Button("Join Video Call");
            videoCallButton.setOnAction(e -> {
                // Find if there's an approved video call with a link
                Optional<String> link = ((Patient)currentUser).viewVideoCalls().stream()
                    .filter(vc -> vc.getStatus().equals("Approved") && vc.getDoctor().equals(chatPartner))
                    .filter(vc -> vc.getLink() != null && !vc.getLink().isEmpty())
                    .map(vc -> vc.getLink())
                    .findFirst();
                
                if (link.isPresent()) {
                    // Try to open the link in browser
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(link.get()));
                    } catch (Exception ex) {
                        showAlert("Cannot open link", "Could not open video call link: " + ex.getMessage());
                    }
                } else {
                    showAlert("No Video Call", "No active video call link available with this doctor.");
                }
            });
            header.getChildren().addAll(avatar, userInfo, videoCallButton);
            HBox.setHgrow(userInfo, Priority.ALWAYS);
        } else {
            header.getChildren().addAll(avatar, userInfo);
        }
        
        return header;
    }
    
    /**
     * Creates the input box for typing and sending messages
     * 
     * @return HBox containing the input elements
     */
    private HBox createInputBox() {
        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(15));
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getStyleClass().add("chat-input-box");
        
        messageField = new TextField();
        messageField.setPromptText("Type your message here...");
        messageField.getStyleClass().add("message-input");
        HBox.setHgrow(messageField, Priority.ALWAYS);
        
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("button-primary");
        
        // Event handlers
        sendButton.setOnAction(e -> sendMessage(messageField.getText()));
        messageField.setOnAction(e -> sendMessage(messageField.getText()));
        
        inputBox.getChildren().addAll(messageField, sendButton);
        return inputBox;
    }

    /**
     * Loads and displays the previous chat history between these users.
     * Retrieves messages from the DataManager and adds them to the chat area.
     */
    private void loadChatHistory() {
        // Clear the chat area first
        chatMessagesBox.getChildren().clear();
        
        // Get fresh chat history directly
        ChatHistory history = DataManager.getChatHistory(currentUser.getId(), chatPartner.getId());
        
        if (history.getMessages().isEmpty()) {
            Label noMessagesLabel = new Label("No previous messages. Start chatting now!");
            noMessagesLabel.setStyle("-fx-text-fill: #888888; -fx-font-style: italic;");
            chatMessagesBox.getChildren().add(noMessagesLabel);
            return;
        }
        
        // Add all messages to the chat area
        for (ChatMessage message : history.getMessages()) {
            appendMessage(message);
        }
        
        // Scroll to bottom after loading history
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        
        // Log messages found
        System.out.println("Loaded " + history.getMessages().size() + " messages for chat between " + 
                          currentUser.getId() + " and " + chatPartner.getId());
    }

    /**
     * Connects to the chat server in a separate thread.
     * Updates the status indicator to show connection state.
     */
    private void connectToServer() {
        new Thread(() -> {
            try {
                int port = ChatManager.getCurrentPort();
                socket = new Socket("localhost", port);
                
                // Create streams
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
                
                // Send user ID as first message
                out.writeObject(currentUser.getId());
                out.flush();
                
                isConnected = true;
                
                // Update UI to show connected status
                Platform.runLater(() -> {
                    statusIndicator.setFill(Color.GREEN);
                    statusLabel.setText("Connected");
                    statusLabel.getStyleClass().add("status-connected");
                });
                
                // Start receiving messages
                receiveMessages();
                
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Connection Failed");
                    showAlert("Connection Error", "Could not connect to chat server: " + e.getMessage());
                });
                System.err.println("Error connecting to chat server: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Starts a separate thread to receive incoming messages.
     * Continuously listens for messages from the chat partner.
     */
    private void receiveMessages() {
        receiveThread = new Thread(() -> {
            try {
                while (isConnected) {
                    Object obj = in.readObject();
                    
                    if (obj instanceof ChatMessage) {
                        ChatMessage message = (ChatMessage) obj;
                        
                        // Only process messages from our chat partner
                        if (message.getSenderId().equals(chatPartner.getId())) {
                            Platform.runLater(() -> appendMessage(message));
                        }
                    }
                }
            } catch (SocketException e) {
                // Socket closed, normal disconnect
                if (isConnected) {
                    System.out.println("Chat connection closed");
                }
            } catch (EOFException e) {
                // End of stream, normal disconnect
                if (isConnected) {
                    System.out.println("Chat connection ended");
                }
            } catch (IOException | ClassNotFoundException e) {
                if (isConnected) {
                    System.err.println("Error receiving messages: " + e.getMessage());
                    Platform.runLater(() -> {
                        statusIndicator.setFill(Color.RED);
                        statusLabel.setText("Connection Lost");
                    });
                }
            } finally {
                disconnect();
            }
        });
        
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    /**
     * Sends a message to the chat partner.
     * Creates a ChatMessage object and sends it to the server.
     * 
     * @param content The text content of the message to send
     */
    private void sendMessage(String content) {
        if (content == null || content.trim().isEmpty()) return;
        
        if (!isConnected) {
            showAlert("Not Connected", "You are not connected to the chat server. Your message will not be delivered.");
            return;
        }
        
        try {
            // Create message object
            ChatMessage message = new ChatMessage(
                currentUser.getId(),
                currentUser.getName(),
                chatPartner.getId(),
                content
            );
            
            // Send message to server
            out.writeObject(message);
            out.flush();
            out.reset(); // Important: reset object stream cache
            
            // Add to UI
            appendMessage(message);
            
            // Clear input field
            messageField.clear();
            
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            showAlert("Send Error", "Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Adds a message to the chat area with appropriate formatting.
     * Different formatting is applied for sent vs. received messages.
     * 
     * @param message The ChatMessage to display
     */
    private void appendMessage(ChatMessage message) {
        HBox messageRow = new HBox(10);
        messageRow.setPadding(new Insets(5, 15, 5, 15));
        
        // Create the message bubble
        VBox messageBubble = new VBox(3);
        Label contentLabel = new Label(message.getContent());
        contentLabel.setWrapText(true);
        
        // Format timestamp
        String timeText = message.getTimestamp().format(TIME_FORMATTER);
        Label timeLabel = new Label(timeText);
        timeLabel.getStyleClass().add("message-time");
        
        messageBubble.getChildren().addAll(contentLabel, timeLabel);
        
        if (message.getSenderId().equals(currentUser.getId())) {
            // Message sent by current user - right aligned
            messageRow.setAlignment(Pos.CENTER_RIGHT);
            messageBubble.getStyleClass().add("chat-bubble");
            messageBubble.getStyleClass().add("chat-bubble-sent");
            messageRow.getChildren().add(messageBubble);
        } else {
            // Message received from chat partner - left aligned
            messageRow.setAlignment(Pos.CENTER_LEFT);
            messageBubble.getStyleClass().add("chat-bubble");
            messageBubble.getStyleClass().add("chat-bubble-received");
            
            // Add sender name for clarity in group chats
            Label senderLabel = new Label(message.getSenderName());
            senderLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
            messageBubble.getChildren().add(0, senderLabel);
            
            messageRow.getChildren().add(messageBubble);
        }
        
        chatMessagesBox.getChildren().add(messageRow);
        
        // Auto-scroll to the new message
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    /**
     * Disconnects from the chat server and cleans up resources.
     * Called when the chat window is closed or connection is lost.
     */
    private void disconnect() {
        if (!isConnected) return;
        
        isConnected = false;
        
        try {
            if (receiveThread != null) {
                receiveThread.interrupt();
            }
            
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
        
        // Update UI to show disconnected status
        Platform.runLater(() -> {
            if (statusIndicator != null && statusLabel != null) {
                statusIndicator.setFill(Color.RED);
                statusLabel.setText("Disconnected");
            }
        });
    }
    
    /**
     * Shows an alert dialog
     * 
     * @param title Alert title
     * @param message Alert message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}