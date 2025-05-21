package com.rpms.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.rpms.GUI.AdminDashboard;
import com.rpms.GUI.DoctorDashboard;
import com.rpms.GUI.PatientDashboard;
import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.Users.User;
import com.rpms.utilities.DataManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the login screen
 * Handles authentication and directing users to appropriate dashboards
 */
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button patientButton;
    
    @FXML
    private Button doctorButton;
    
    @FXML
    private Button adminButton;
    
    @FXML
    private Label selectedRoleLabel;
    
    private String selectedRole = null;
    
    /**
     * Initializes the controller
     * Sets up UI components and loads necessary data
     */
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        
        // Clear error message when user types
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });
        
        // Ensure data directory exists
        ensureDataDirectoryExists();
        
        // Initialize data if needed
        try {
            initializeData();
        } catch (Exception e) {
            System.err.println("Error initializing data: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading system data: " + e.getMessage());
        }
        
        // Debug: Print current users to verify data loading
        System.out.println("Current users in system:");
        for (User user : Administrator.getAllUsers()) {
            try {
                System.out.println("User: " + user.getName() + 
                           ", Username: " + user.getClass().getDeclaredField("username").get(user) +
                           ", Role: " + user.getRole());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.err.println("Error accessing user details: " + e.getMessage());
            }
        }
        
        // Configure window close event to save data
        Platform.runLater(() -> {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                try {
                    System.out.println("Application is closing. Saving all data...");
                    DataManager.saveAllData();
                    System.out.println("Data saved successfully.");
                    Platform.exit();
                    System.exit(0);  // Force JVM shutdown
                } catch (Exception e) {
                    System.err.println("Error saving data: " + e.getMessage());
                }
            });
        });
    }
    
    /**
     * Handles patient role button click
     */
    @FXML
    public void handlePatientRoleSelect() {
        selectedRole = "Patient";
        updateSelectedRoleUI();
    }
    
    /**
     * Handles doctor role button click
     */
    @FXML
    public void handleDoctorRoleSelect() {
        selectedRole = "Doctor";
        updateSelectedRoleUI();
    }
    
    /**
     * Handles admin role button click
     */
    @FXML
    public void handleAdminRoleSelect() {
        selectedRole = "Administrator";
        updateSelectedRoleUI();
    }
    
    /**
     * Updates UI based on selected role
     */
    private void updateSelectedRoleUI() {
        selectedRoleLabel.setText("Selected Role: " + selectedRole);
        errorLabel.setVisible(false);
        
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
     * Handles the login button click
     * Authenticates user and navigates to appropriate dashboard
     */
    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty");
            return;
        }
        
        if (selectedRole == null) {
            showError("Please select a role");
            return;
        }
        
        // Debug: Print login attempt details
        System.out.println("Login attempt - Username: " + username + ", Role: " + selectedRole);
        
        // Debug: Print all available users for comparison
        System.out.println("Available users:");
        for (User user : Administrator.getAllUsers()) {
            System.out.println("- User: " + user.getName() + 
                       ", Username: " + user.getUsername() + 
                       ", Role: " + user.getRole() + 
                       ", Type: " + user.getClass().getSimpleName());
        }
        
        // Try the hardcoded values from README for debugging
        if (selectedRole.equals("Doctor") && username.equals("kshabbir.doc") && password.equals("kshabbir123")) {
            System.out.println("Using hardcoded doctor credentials for testing");
            // Find the doctor in the system
            for (User user : Administrator.getAllUsers()) {
                if (user instanceof Doctor) {
                    Doctor doctor = (Doctor) user;
                    try {
                        new DoctorDashboard(doctor).start(new Stage());
                        Stage currentStage = (Stage) loginButton.getScene().getWindow();
                        currentStage.close();
                        return;
                    } catch (Exception e) {
                        showError("Error loading dashboard: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (selectedRole.equals("Patient") && username.equals("akhan") && password.equals("akhan123")) {
            System.out.println("Using hardcoded patient credentials for testing");
            // Find the patient in the system
            for (User user : Administrator.getAllUsers()) {
                if (user instanceof Patient) {
                    Patient patient = (Patient) user;
                    try {
                        new PatientDashboard(patient).start(new Stage());
                        Stage currentStage = (Stage) loginButton.getScene().getWindow();
                        currentStage.close();
                        return;
                    } catch (Exception e) {
                        showError("Error loading dashboard: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (selectedRole.equals("Administrator") && username.equals("agohar.adm") && password.equals("502082.default.adm")) {
            System.out.println("Using hardcoded admin credentials for testing");
            try {
                new AdminDashboard().start(new Stage());
                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();
                return;
            } catch (Exception e) {
                showError("Error loading dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Standard authentication
        User user = authenticateUser(username, password, selectedRole);
        
        if (user == null) {
            showError("Invalid username or password");
            // Log the failed login attempt
            Administrator.addSystemLog("Failed login attempt for username '" + username + "' with role '" + selectedRole + "'.");
        }
        else {
            // Log successful login
            Administrator.addSystemLog("Successful login for user: " + user.getName() + " (" + selectedRole + ")");
            
            try {
                switch (selectedRole) {
                    case "Patient":
                        // Use the FXML-based dashboard loading method
                        com.rpms.Main.showPatientDashboard((Patient) user);
                        break;
                    case "Doctor":
                        new DoctorDashboard((Doctor) user).start(new Stage());
                        break;
                    case "Administrator":
                        new AdminDashboard().start(new Stage());
                        break;
                }
                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();
            } catch (Exception e) {
                showError("Error loading dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
     * Authenticates a user based on username, password and role
     * @param username The username to authenticate
     * @param password The password to verify
     * @param role The role of the user (Patient, Doctor, Administrator)
     * @return User object if authentication successful, null otherwise
     */
    private User authenticateUser(String username, String password, String role) {
        ArrayList<User> users = Administrator.getAllUsers();
        System.out.println("Attempting authentication with: " + username + ", " + role);
        System.out.println("Total users to check: " + users.size());
        
        for (User user : users) {
            // Use the built-in methods rather than reflection
            boolean usernameMatch = user.checkUsername(username);
            boolean passwordMatch = user.checkPassword(password);
            boolean roleMatch = user.getRole().equals(role);
            
            // Debug output (don't print actual password)
            System.out.println("Checking user: " + user.getName() + 
                       " | Username match: " + usernameMatch + 
                       " | Password match: " + passwordMatch +
                       " | Role match: " + roleMatch);
            
            if (usernameMatch && passwordMatch && roleMatch) {
                System.out.println("Authentication successful for: " + user.getName());
                return user;
            }
        }
        
        System.out.println("Authentication failed: No matching user found");
        return null;
    }   /**
     * Initializes data required for the application
     * Loads users and other necessary data
     */
    private void initializeData() {
        try {
            // Ensure data directory exists and is writable
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdir();
                if (created) {
                    System.out.println("Data directory created successfully.");
                } else {
                    System.err.println("Failed to create data directory!");
                    // Try to create in a different location if permissions are an issue
                    dataDir = new File(System.getProperty("user.home") + File.separator + "rpms_data");
                    if (!dataDir.exists()) {
                        dataDir.mkdir();
                        System.out.println("Created alternative data directory: " + dataDir.getAbsolutePath());
                    }
                }
            }
            
            // Test write permissions
            try {
                File testFile = new File(dataDir, "test_write.tmp");
                if (testFile.createNewFile()) {
                    testFile.delete();
                    System.out.println("Data directory is writable");
                } else {
                    System.err.println("Could not write to data directory!");
                }
            } catch (IOException e) {
                System.err.println("Data directory write test failed: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Load all saved data using DataManager
            DataManager.loadAllData();
            System.out.println("Data loading attempt completed");
            
            // If no users exist, create default users for testing
            if (Administrator.getAllUsers().isEmpty()) {
                System.out.println("Creating default users...");
                
                // Create doctor
                Doctor doctor = new Doctor("doc1", "Rohaan", "+92-300-8663789", 
                    "umartanveer8870@gmail.com", "rohaan_doc1", "rohaan123");
                
                // Create patient and assign to doctor
                Patient patient = new Patient("pat1", "Hamza Saeed", "+92-317-9886367", 
                    "umartanveer8870@gmail.com", "hamza", "hamza123", 
                    new ArrayList<>(), doctor);
                
                // Create admin
                Administrator admin = new Administrator("adm1", "Umar Tanveer", 
                    "+92-316-5666994", "utanveer@gmail.com", "ut_adm", "adm123");
                
                // Register users
                Administrator.registerPatient(patient);
                Administrator.registerDoctor(doctor);
                Administrator.registerAdministrator(admin);
                
                // Save the sample data
                DataManager.saveAllData();
                System.out.println("Created and saved default users");
                
                // Print created users for verification
                System.out.println("Created users:");
                for (User user : Administrator.getAllUsers()) {
                    System.out.println("- " + user.getName() + " (" + user.getRole() + 
                               "), Username: " + user.getUsername());
                }
            } else {
                System.out.println("Loaded existing users: " + Administrator.getAllUsers().size());
            }
        } catch (Exception e) {
            System.err.println("Error initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    /**
     * Ensures that the data directory exists
     * Creates it if it doesn't exist
     */
    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdir();
            if (created) {
                System.out.println("Data directory created successfully.");
            } else {
                System.err.println("Failed to create data directory.");
            }
        }
    }
    
    /**
     * Displays an error message to the user
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
