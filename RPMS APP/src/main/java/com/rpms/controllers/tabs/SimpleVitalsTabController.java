package com.rpms.controllers.tabs;

import com.rpms.HealthData.VitalSign;
import com.rpms.Users.Patient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

/**
 * A simplified controller for the vitals tab - uses less UI elements 
 * to reduce chances of FXML errors
 */
public class SimpleVitalsTabController {
    
    @FXML private BorderPane vitalsRoot;
    @FXML private Button uploadVitalsButton;
    @FXML private ListView<VitalSign> vitalsListView;
    
    private Patient patient;
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        System.out.println("SimpleVitalsTabController initialized");
    }
    
    /**
     * Set the patient for this tab
     * 
     * @param patient The patient whose data to display
     */
    public void setPatient(Patient patient) {
        System.out.println("Setting patient for SimpleVitalsTabController: " + 
                          (patient != null ? patient.getName() : "null"));
        this.patient = patient;
        loadVitalsData();
    }
    
    /**
     * Load vital signs data
     */
    public void loadVitalsData() {
        if (patient != null && vitalsListView != null) {
            System.out.println("Loading vitals data for: " + patient.getName());
            ObservableList<VitalSign> vitalsData = 
                FXCollections.observableArrayList(patient.viewPreviousVitals());
            vitalsListView.setItems(vitalsData);
            System.out.println("Loaded " + vitalsData.size() + " vital records");
        } else {
            System.err.println("Cannot load vitals: " + 
                              (patient == null ? "patient is null" : "vitalsListView is null"));
        }
    }
}
