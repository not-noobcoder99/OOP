package com.rpms.GUI;

import java.io.File;
import java.util.ArrayList;

import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.Users.User;
import com.rpms.utilities.DataManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The initial login screen for all users of the system.
 * Handles authentication and directs users to appropriate dashboards.
 */
public class LoginScreen extends Application {

    private String selectedRole = null;
    private Label selectedRoleLabel;
    private Button patientButton;
    private Button doctorButton;
    private Button adminButton;

    /**
     * Initializes and displays the login screen interface.
     * Sets up the GUI components and event handlers.
     *
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("Remote Health Monitoring System - Login");

        // Creating GUI elements
        Label roleLabel = new Label("Select Your Role:");
        
        // Role selection buttons
        patientButton = new Button("Patient");
        patientButton.setGraphic(new Label("🏥"));
        patientButton.setMaxWidth(Double.MAX_VALUE);
        patientButton.getStyleClass().add("role-button");
        
        doctorButton = new Button("Doctor");
        doctorButton.setGraphic(new Label("👨‍⚕️"));
        doctorButton.setMaxWidth(Double.MAX_VALUE);
        doctorButton.getStyleClass().add("role-button");
        
        adminButton = new Button("Administrator");
        adminButton.setGraphic(new Label("👤"));
        adminButton.setMaxWidth(Double.MAX_VALUE);
        adminButton.getStyleClass().add("role-button");
        
        // Role selection event handlers
        patientButton.setOnAction(e -> {
            selectedRole = "Patient";
            updateSelectedRoleUI();
        });
        
        doctorButton.setOnAction(e -> {
            selectedRole = "Doctor";
            updateSelectedRoleUI();
        });
        
        adminButton.setOnAction(e -> {
            selectedRole = "Administrator";
            updateSelectedRoleUI();
        });
        
        // Role buttons horizontal layout
        HBox roleButtonsBox = new HBox(10, patientButton, doctorButton, adminButton);
        roleButtonsBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(patientButton, Priority.ALWAYS);
        HBox.setHgrow(doctorButton, Priority.ALWAYS);
        HBox.setHgrow(adminButton, Priority.ALWAYS);

        // Selected role indicator
        selectedRoleLabel = new Label("No role selected");
        selectedRoleLabel.setStyle("-fx-text-fill: #0d47a1;");

        Label usernameLabel = new Label("Username:");
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Button loginButton = new Button("Login");
        loginButton.setGraphic(new Label("🔑"));
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.getStyleClass().add("button-primary");

        // Title header
        Label titleLabel = new Label("Remote Health Monitoring System");
        titleLabel.getStyleClass().add("dashboard-title");

        HBox headerBox = new HBox();
        headerBox.getStyleClass().add("dashboard-header");
        headerBox.getChildren().add(titleLabel);
        headerBox.setPrefWidth(Double.MAX_VALUE);
        headerBox.setAlignment(Pos.CENTER);

        VBox formContainer = new VBox(10);
        formContainer.getStyleClass().add("login-container");
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.getChildren().addAll(
            roleLabel, roleButtonsBox, selectedRoleLabel,
            usernameLabel, usernameTextField,
            passwordLabel, passwordField,
            loginButton
        );
        
        // Center the form on screen
        BorderPane centeringPane = new BorderPane();
        centeringPane.setCenter(formContainer);
        centeringPane.setPadding(new Insets(40, 20, 20, 20));
        
        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(headerBox);
        mainLayout.setCenter(centeringPane);
            
        loginButton.setOnAction(e -> {
            String enteredUsername = usernameTextField.getText();
            String enteredPassword = passwordField.getText();

            if (selectedRole == null) {
                showAlert("Please select a role.");
                return;
            }

            User user = authenticateUser(enteredUsername, enteredPassword, selectedRole);

            if (user == null) {
                showAlert("Invalid username or password.");
                // Log the failed login attempt
                Administrator.addSystemLog("Failed login attempt for username '" + enteredUsername + "' with role '" + selectedRole + "'.");
                return;
            }
            else{
                // Log the successful login
                Administrator.addSystemLog("Successful login for username '" + enteredUsername + "' with role '" + selectedRole + "'.");
            }

            // Open the correct dashboard
            switch (selectedRole) {
                case "Patient":
                    // Use Main.showPatientDashboard instead of direct instantiation
                    com.rpms.Main.showPatientDashboard((Patient) user);
                    break;
                case "Doctor":
                    new DoctorDashboard((Doctor) user).start(new Stage());
                    break;
                case "Administrator":
                    new AdminDashboard().start(new Stage());
                    break;
            }
            primaryStage.close();
        });

        Scene scene = new Scene(mainLayout, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);  // Force JVM shutdown
        });
        
        String cssPath = com.rpms.Main.getStylesheetPath();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }

        primaryStage.show();
    }
    
    /**
     * Updates the UI based on the selected role
     */
    private void updateSelectedRoleUI() {
        selectedRoleLabel.setText("Selected Role: " + selectedRole);
        
        // Reset all buttons to default style
        patientButton.getStyleClass().remove("selected-role-button");
        doctorButton.getStyleClass().remove("selected-role-button");
        adminButton.getStyleClass().remove("selected-role-button");
        
        // Apply selected style to chosen button
        if (selectedRole.equals("Patient")) {
            patientButton.getStyleClass().add("selected-role-button");
        } else if (selectedRole.equals("Doctor")) {
            doctorButton.getStyleClass().add("selected-role-button");
        } else if (selectedRole.equals("Administrator")) {
            adminButton.getStyleClass().add("selected-role-button");
        }
    }

    /**
     * Displays an alert dialog with the given message.
     * 
     * @param message The message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Main method to launch the application directly from this class.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Authenticates a user against the system database.
     * Checks if the provided credentials match any user with the specified role.
     * 
     * @param username The username entered by the user
     * @param password The password entered by the user
     * @param role The selected role (Patient, Doctor, or Administrator)
     * @return The authenticated User object or null if authentication fails
     */
    public User authenticateUser(String username, String password, String role) {
        ArrayList<User> users = Administrator.getAllUsers();
        System.out.println("Attempting authentication with: " + username + ", " + role);
        System.out.println("Total users to check: " + users.size());
        
        for (User user : users) {
            // Debug output before checking
            System.out.println("Checking user: " + user.getName() + ", Role: " + user.getRole() + ", Username: " + user.getUsername());
            
            // Use the built-in methods
            boolean usernameMatch = user.checkUsername(username);
            boolean passwordMatch = user.checkPassword(password);
            boolean roleMatch = user.getRole().equals(role);
            
            System.out.println("Results - Username match: " + usernameMatch + 
                      ", Password match: " + passwordMatch + 
                      ", Role match: " + roleMatch);
            
            if (usernameMatch && passwordMatch && roleMatch) {
                System.out.println("Authentication successful for: " + user.getName());
                return user;
            }
        }
        
        System.out.println("Authentication failed: No matching user found");
        return null;
    }

@Override
public void init() throws Exception {
    // Create data directory if it doesn't exist
    File dataDir = new File("data");
    if (!dataDir.exists()) {
        dataDir.mkdir();
        System.out.println("Created data directory: " + dataDir.getAbsolutePath());
    }
    
    // First try to load existing data
    DataManager.loadAllData();
    
    // Only create sample data if no users exist
    if (Administrator.getAllUsers().isEmpty()) {
        System.out.println("No existing users found. Creating sample data...");
        createSampleData();
        
        // Save the sample data
        DataManager.saveAllData();
        
        // Load again to verify
        DataManager.loadAllData();
    } else {
        System.out.println("Loaded " + Administrator.getAllUsers().size() + " existing users");
    }
    
    System.out.println("System initialized with " + Administrator.getAllUsers().size() + " total users");
    System.out.println("Available users:");
    for (User user : Administrator.getAllUsers()) {
        System.out.println("- " + user.getName() + " (" + user.getRole() + "), Username: " + user.getUsername());
    }
}

    /**
     * Creates sample data for the system when running for the first time.
     * Adds a default doctor, patient, and administrator.
     */
    private void createSampleData() {
        // same email is used for checking purposes
        Doctor doctor = new Doctor("doc1", "Rohaan", "+92-300-8663789", "umartanveer8870@gmail.com", "rohaan_doc1", "rohaan123");
        Patient patient = new Patient("pat1", "Hamza Saeed", "+92-317-9886367", "umartanveer8870@gmail.com", "hamza", "hamza123", new ArrayList<>(), doctor);
        Administrator admin = new Administrator("adm1", "Umar Tanveer", "+92-316-5666994", "utanveer@gmail.com", "ut_adm", "adm123");

        // Register users
        Administrator.registerPatient(patient);
        Administrator.registerDoctor(doctor);
        Administrator.registerAdministrator(admin);
    }

    /**
     * Clean up resources when the application is stopping.
     * Saves all data before shutting down.
     * 
     * @throws Exception If an error occurs during shutdown
     */
    @Override
    public void stop() throws Exception {
        System.out.println("Application is closing. Saving all data...");
        DataManager.saveAllData();
        System.out.println("Data saved successfully.");
        super.stop();
    }
}
