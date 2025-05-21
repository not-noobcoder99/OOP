package com.rpms;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.rpms.GUI.LoginScreen;
import com.rpms.GUI.PatientDashboard;
import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.controllers.DoctorDashboardController;
import com.rpms.controllers.PatientDashboardController;
import com.rpms.utilities.ChatManager;
import com.rpms.utilities.DataManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Main application class for the Remote Patient Monitoring System.
 * Entry point for the application, handles startup, shutdown and navigation.
 */
public class Main extends Application {
    
    private static Stage primaryStage;
    
    /**
     * Main application entry point
     * Initializes the system and shows the login screen
     */
    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        
        try {
            // First ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                System.out.println("Created data directory: " + created);
            }
            
            // Initialize admin helpers
      //      com.rpms.utilities.AdminHelper.ensureAdminInitialization();
            
            // Try to load existing data
            DataManager.loadAllData();
            
            // Check if we have users, create sample data if needed
            if (Administrator.getAllUsers() == null || Administrator.getAllUsers().isEmpty()) {
                System.out.println("No users found. Creating sample users...");
                
                // Create sample users
                Doctor doctor = new Doctor("doc1", "Rohaan", "+92-300-8663789", 
                    "umartanveer8870@gmail.com", "rohaan_doc1", "rohaan123");
                
                Patient patient = new Patient("pat1", "Hamza Saeed", "+92-317-9886367", 
                    "umartanveer8870@gmail.com", "hamza", "hamza123", 
                    new ArrayList<>(), doctor);
                    
                Administrator admin = new Administrator("adm1", "Umar Tanveer", 
                    "+92-316-5666994", "utanveer@gmail.com", "ut_adm", "adm123");
                
                // Register users
                Administrator.registerPatient(patient);
                Administrator.registerDoctor(doctor);
                Administrator.registerAdministrator(admin);
                
                // Save data
                DataManager.saveAllData();
                System.out.println("Sample data created and saved.");
            }
            
            // Print diagnostics
          //  com.rpms.utilities.AdminHelper.printUserDiagnostics();
            
            // Initialize chat manager
            ChatManager.initialize();
            
            // Show login screen
            showLoginScreen();
        } catch (Exception e) {
            System.err.println("Error during application startup: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Startup Error", "Failed to start application: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Shows the login screen
     */
    public static void showLoginScreen() {
        try {
            new LoginScreen().start(primaryStage);
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to load login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the patient dashboard for the given patient
     * @param patient The patient to show the dashboard for
     */
    public static void showPatientDashboard(Patient patient) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/PatientDashboard.fxml"));
            Parent root = loader.load();
            
            // Get the controller
            PatientDashboardController controller = loader.getController();
            
            // Initialize data in the controller
            controller.initializeData(patient);
            
            // Create a new stage
            Stage stage = new Stage();
            stage.setTitle("Patient Dashboard - " + patient.getName());
            
            // Set the scene
            Scene scene = new Scene(root);
            String css = getStylesheetPath();
            if (css != null) {
                scene.getStylesheets().add(css);
            }
            stage.setScene(scene);
            
            // Handle window close to save data
            stage.setOnCloseRequest(e -> {
                try {
                    DataManager.saveAllData();
                } catch (Exception ex) {
                    System.err.println("Error saving data on close: " + ex.getMessage());
                }
            });
            
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error showing patient dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to legacy dashboard as a last resort
            try {
                new PatientDashboard(patient).start(new Stage());
            } catch (Exception ex) {
                System.err.println("Fatal error showing any dashboard: " + ex.getMessage());
                showErrorAlert("Fatal Error", "Could not load patient dashboard: " + e.getMessage());
            }
        }
    }
    
    /**
     * Shows the doctor dashboard with the specified doctor
     * 
     * @param doctor The doctor to display the dashboard for
     */
    public static void showDoctorDashboard(Doctor doctor) {
        try {
            System.out.println("Loading FXML doctor dashboard for: " + doctor.getName());
            
            // Attempt to load the FXML file
            String doctorDashboardPath = "/fxml/DoctorDashboard.fxml";
            System.out.println("Attempting to load: " + doctorDashboardPath);
            
            URL fxmlUrl = Main.class.getResource(doctorDashboardPath);
            if (fxmlUrl == null) {
                System.err.println("ERROR: Doctor Dashboard FXML URL is null. Could not find resource: " + doctorDashboardPath);
                
                // Try alternative paths
                testAlternativePaths("DoctorDashboard.fxml");
                
                // Fall back to non-FXML version if needed
                System.out.println("Falling back to non-FXML Doctor Dashboard...");
                new com.rpms.GUI.DoctorDashboard(doctor).start(new Stage());
                return;
            }
            
            System.out.println("DoctorDashboard.fxml URL found: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // Get the controller and initialize it with the doctor data
            DoctorDashboardController controller = loader.getController();
            controller.initializeData(doctor);
            
            Scene scene = new Scene(root);
            String css = getStylesheetPath();
            if (css != null) {
                scene.getStylesheets().add(css);
            }
            
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Remote Patient Monitoring System - Doctor Dashboard");
            dashboardStage.setScene(scene);
            dashboardStage.setResizable(true);
            dashboardStage.show();
            
            // Setup close handler to save data
            dashboardStage.setOnCloseRequest(event -> {
                try {
                    System.out.println("Saving data before closing...");
                    DataManager.saveAllData();
                } catch (Exception e) {
                    System.err.println("Error saving data: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error loading doctor dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Try fallback to non-FXML version
            try {
                System.out.println("Falling back to non-FXML Doctor Dashboard...");
                new com.rpms.GUI.DoctorDashboard(doctor).start(new Stage());
            } catch (Exception ex) {
                System.err.println("Fallback also failed: " + ex.getMessage());
                ex.printStackTrace();
                
                // Show an error dialog
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Error loading dashboard: " + e.getMessage()
                );
                alert.showAndWait();
            }
        }
    }
    
    /**
     * Debug method to test alternative resource paths
     */
    private static void testAlternativePaths(String filename) {
        // Test different path options
        String[] pathOptions = {
            "/" + filename,
            "fxml/" + filename,
            "/fxml/" + filename,
            "src/main/resources/fxml/" + filename,
            "../resources/fxml/" + filename,
            "./fxml/" + filename
        };
        
        System.out.println("\n--- Testing alternative paths for " + filename + " ---");
        for (String path : pathOptions) {
            URL url = Main.class.getResource(path);
            System.out.println("Path: " + path + " => " + (url != null ? "FOUND" : "not found"));
        }
        
        // Also check if the physical file exists
        String projectRoot = System.getProperty("user.dir");
        System.out.println("\n--- Checking physical file existence ---");
        String[] fileLocations = {
            projectRoot + "/src/main/resources/fxml/" + filename,
            projectRoot + "/target/classes/fxml/" + filename
        };
        
        for (String location : fileLocations) {
            File file = new File(location);
            System.out.println("File: " + location + " => " + (file.exists() ? "EXISTS" : "does not exist"));
        }
    }
    
    /**
     * Debug method to check the availability of key resources
     */
    private static void debugResourceAvailability() {
        System.out.println("\n=== Debugging Resource Availability ===");
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        
        // Check for vital files
        String[] resources = {
            "/fxml/PatientDashboard.fxml",
            "/fxml/tabs/VitalsTab.fxml",
                "/css/default.css"
        };
        
        for (String resource : resources) {
            URL url = Main.class.getResource(resource);
            System.out.println("Resource: " + resource + " => " + (url != null ? "FOUND at " + url : "NOT FOUND"));
        }
        System.out.println("===================================\n");
    }
    
    /**
     * Initializes the application styling
     */
    private static void initializeAppStyling() {
        try {
            // Print working directory for debugging
            System.out.println("Working directory: " + System.getProperty("user.dir"));
            
            // Print available resources
            System.out.println("Looking for CSS at: " + "/css/default.css");
            URL cssUrl = Main.class.getResource("/css/default.css");
            System.out.println("CSS URL: " + cssUrl);
            
            // Check if CSS file exists in resources
            if (cssUrl == null) {
                System.err.println("Warning: CSS file not found in resources: " + "/css/default.css");
                
                // Try alternative paths
                testAlternativePaths("default.css");
            }
        } catch (Exception e) {
            System.err.println("Error initializing styling: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the path to the stylesheet
     * 
     * @return The path to the stylesheet
     */
    public static String getStylesheetPath() {
        try {
            String cssPath = Paths.get("src/main/resources/css/default.css").toUri().toString();
            return cssPath;
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Logs out the current user and shows the login screen
     */
    public static void logout() {
        // Save data before logout
        try {
            DataManager.saveAllData();
        } catch (Exception e) {
            System.err.println("Error saving data during logout: " + e.getMessage());
        }
        
        // Show login screen
        Platform.runLater(() -> {
            showLoginScreen();
        });
    }
    
    /**
     * Shows an error alert with the specified title and message
     * 
     * @param title The title of the error alert
     * @param message The message to display in the error alert
     */
    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}