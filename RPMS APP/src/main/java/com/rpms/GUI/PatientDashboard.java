// GUI/PatientDashboard.java
package com.rpms.GUI;

import com.rpms.Main;
import com.rpms.Users.Patient;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Dashboard interface for patient users.
 * Now acts as a bridge to the FXML-based implementation.
 */
public class PatientDashboard extends Application {

    /** The patient user who is currently logged in */
    private Patient patient;

    /**
     * Creates a new patient dashboard for the specified patient.
     * 
     * @param patient The patient user who logged in
     */
    public PatientDashboard(Patient patient) {
        this.patient = patient;
    }

    /**
     * Initializes and displays the patient dashboard interface.
     * Now delegates to the Main.showPatientDashboard method that uses FXML.
     * 
     * @param stage The JavaFX stage to display the dashboard
     */
    @Override
    public void start(Stage stage) {
        try {
            // Delegate to the FXML-based implementation
            Main.showPatientDashboard(patient);
        } catch (Exception e) {
            System.err.println("Error showing patient dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // If there's an error, show an error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Dashboard Error");
            alert.setContentText("Error loading dashboard: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
