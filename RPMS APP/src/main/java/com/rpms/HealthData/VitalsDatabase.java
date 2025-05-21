package com.rpms.HealthData;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * Manages a collection of vital signs for a patient.
 * Provides functionality for storing, retrieving, and visualizing vital sign data.
 * Implements Serializable to allow persistence of vital sign records.
 */
public class VitalsDatabase implements Serializable {
    /** Collection of all vital signs for a patient */
    private ArrayList<VitalSign> vitals = new ArrayList<>();
    
    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /** Transient UI elements for visualization (not serialized) */
    private transient Scene scene;
    private transient Stage stage;

    // ===== Getters and Setters for UI Components =====
    
    /**
     * Gets the JavaFX scene used for visualization
     * @return Scene object
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * Sets the JavaFX scene for visualization
     * @param scene Scene to use for visualization
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    /**
     * Gets the JavaFX stage used for visualization
     * @return Stage object
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Sets the JavaFX stage for visualization
     * @param stage Stage to use for visualization
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // ===== Vital Signs Collection Management =====
    
    /**
     * Gets all vital signs in this database
     * @return ArrayList of vital sign objects
     */
    public ArrayList<VitalSign> getVitals() {
        if (vitals == null) {
            vitals = new ArrayList<>();
        }
        return vitals;
    }

    /**
     * Adds a new vital sign to the database
     * @param vital VitalSign to add
     */
    public void addVital(VitalSign vital) {
        vitals.add(vital);
        System.out.println("Vital sign added to database.");
    }
    
    /**
     * Removes a vital sign from the database
     * @param vital VitalSign to remove
     */
    public void removeVital(VitalSign vital) {
        vitals.remove(vital);
        System.out.println("Vital sign removed from database.");
    }

    /**
     * Generates and displays a graphical visualization of vital signs over time.
     * Creates a line chart showing trends in heart rate, oxygen levels, and temperature.
     * 
     * @param stage JavaFX stage to display the visualization
     */
    public void generateVitalsGraph(Stage stage) {
        if (vitals.isEmpty()) {
            System.out.println("No vitals to display.");
            return;
        }

        // Common X-axis (DateTime)
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("DateTime");

        // Y-axis for values
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");

        // LineChart
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Vital Signs Over Time");

        // Data series
        XYChart.Series<String, Number> heartRateSeries = new XYChart.Series<>();
        heartRateSeries.setName("Heart Rate (bpm)");

        XYChart.Series<String, Number> oxygenLevelSeries = new XYChart.Series<>();
        oxygenLevelSeries.setName("Oxygen Level (%)");

        XYChart.Series<String, Number> temperatureSeries = new XYChart.Series<>();
        temperatureSeries.setName("Temperature (°C)");

        // Formatter for timestamps
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm");

        for (VitalSign vital : vitals) {
            String time = vital.getDateTimeRecorded().format(formatter);
            heartRateSeries.getData().add(new XYChart.Data<>(time, vital.getHeartRate()));
            oxygenLevelSeries.getData().add(new XYChart.Data<>(time, vital.getOxygenLevel()));
            temperatureSeries.getData().add(new XYChart.Data<>(time, vital.getTemperature()));
        }

        lineChart.getData().add(heartRateSeries);
        lineChart.getData().add(oxygenLevelSeries);
        lineChart.getData().add(temperatureSeries);

        // Show chart
        VBox vbox = new VBox(lineChart);
        Scene scene = new Scene(vbox, 800, 600);
        stage.setTitle("Patient Vital Signs Visualization");
        stage.setScene(scene);
        stage.show();
    }

    
    /**
     * Custom deserialization method to handle transient fields
     * Initializes UI components after deserialization
     * 
     * @param in ObjectInputStream to read from
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If class of a serialized object cannot be found
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Initialize transient fields
        this.scene = null;
        this.stage = null;
    }
}
