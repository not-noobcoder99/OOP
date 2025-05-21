package com.rpms.controllers.tabs;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rpms.HealthData.VitalSign;
import com.rpms.Users.Patient;
import com.rpms.utilities.DateUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Controller for the Vitals Tab of the Patient Dashboard.
 * Manages display and interaction with vital signs data.
 */
public class VitalsTabController {

    // UI Elements from FXML
    @FXML private BorderPane vitalsRoot;
    @FXML private ListView<VitalSign> vitalsListView;
    @FXML private Button uploadVitalsButton;
    @FXML private Button addVitalButton;
    @FXML private Button removeVitalButton;
    @FXML private TabPane graphTabPane;
    
    // Charts
    @FXML private LineChart<String, Number> heartRateChart;
    @FXML private LineChart<String, Number> bloodPressureChart;
    @FXML private LineChart<String, Number> oxygenChart;
    @FXML private LineChart<String, Number> temperatureChart;
    
    // Latest vital signs display labels
    @FXML private Label heartRateLabel;
    @FXML private Label heartRateStatusLabel;
    @FXML private Label bpLabel;
    @FXML private Label bpStatusLabel;
    @FXML private Label o2Label;
    @FXML private Label o2StatusLabel;
    @FXML private Label tempLabel;
    @FXML private Label tempStatusLabel;
    
    // Reference to the patient
    private Patient patient;
    
    // Data for charts
    private XYChart.Series<String, Number> heartRateSeries;
    private XYChart.Series<String, Number> systolicSeries;
    private XYChart.Series<String, Number> diastolicSeries;
    private XYChart.Series<String, Number> oxygenSeries;
    private XYChart.Series<String, Number> temperatureSeries;
    
    // Constants for normal vital ranges
    private static final int MIN_HEART_RATE = 60;
    private static final int MAX_HEART_RATE = 100;
    private static final int MIN_SYSTOLIC = 90;
    private static final int MAX_SYSTOLIC = 120;
    private static final int MIN_DIASTOLIC = 60;
    private static final int MAX_DIASTOLIC = 80;
    private static final int MIN_OXYGEN = 95;
    private static final int MAX_OXYGEN = 100;
    private static final double MIN_TEMP = 36.5;
    private static final double MAX_TEMP = 37.5;
    
    /**
     * Initializes the controller after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("VitalsTabController initializing...");
        
        // Initialize chart series
        heartRateSeries = new XYChart.Series<>();
        systolicSeries = new XYChart.Series<>();
        systolicSeries.setName("Systolic");
        diastolicSeries = new XYChart.Series<>();
        diastolicSeries.setName("Diastolic");
        oxygenSeries = new XYChart.Series<>();
        temperatureSeries = new XYChart.Series<>();
        
        // Add series to charts
        heartRateChart.getData().add(heartRateSeries);
        bloodPressureChart.getData().addAll(systolicSeries, diastolicSeries);
        oxygenChart.getData().add(oxygenSeries);
        temperatureChart.getData().add(temperatureSeries);
        
        // Set up listeners and actions
        setupButtonActions();
        setupListView();
        
        System.out.println("VitalsTabController initialized.");
    }
    
    /**
     * Sets up the patient data for the controller
     * @param patient The patient whose vital signs to display
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
        loadVitalsData();
    }
    
    /**
     * Sets up the button actions for upload, add, and remove vital buttons
     */
    private void setupButtonActions() {
        // Upload button - for CSV uploads
        uploadVitalsButton.setOnAction(e -> handleUploadVitals());
        
        // Add button - for manual vital entry
        addVitalButton.setOnAction(e -> handleAddVital());
        
        // Remove button - to delete selected vital sign
        removeVitalButton.setOnAction(e -> handleRemoveVital());
    }
    
    /**
     * Sets up the list view for vital signs
     */
    private void setupListView() {
        vitalsListView.setCellFactory(param -> new ListCell<VitalSign>() {
            @Override
            protected void updateItem(VitalSign vital, boolean empty) {
                super.updateItem(vital, empty);
                
                if (empty || vital == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a layout for each vital sign entry
                    HBox cell = new HBox(10);
                    cell.setAlignment(Pos.CENTER_LEFT);
                    
                    // Date/time display
                    Label dateLabel = new Label(DateUtil.format(vital.getDateTimeRecorded()));
                    dateLabel.setStyle("-fx-font-weight: bold;");
                    dateLabel.setPrefWidth(150);
                    
                    // Vital values display
                    VBox valuesBox = new VBox(2);
                    valuesBox.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(valuesBox, Priority.ALWAYS);
                    
                    Label valuesLabel = new Label(
                        String.format("HR: %.0f bpm | BP: %s | O₂: %.0f%% | Temp: %.1f°C", 
                        vital.getHeartRate(), vital.getBloodPressure(), 
                        vital.getOxygenLevel(), vital.getTemperature())
                    );
                    
                    valuesBox.getChildren().add(valuesLabel);
                    
                    // Add status indicators if any values are out of range
                    if (isAnyVitalOutOfRange(vital)) {
                        Label alertLabel = new Label("⚠ Some values out of normal range");
                        alertLabel.setStyle("-fx-text-fill: #ff8800; -fx-font-size: 10px;");
                        valuesBox.getChildren().add(alertLabel);
                    }
                    
                    cell.getChildren().addAll(dateLabel, valuesBox);
                    setGraphic(cell);
                    setText(null);
                }
            }
        });
        
        // Add selection listener to highlight corresponding graph points
        vitalsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Find and highlight this point in the charts
                highlightDataPoint(newSelection);
            }
        });
    }
    
    /**
     * Loads all vital signs data for the patient
     */
    public void loadVitalsData() {
        if (patient == null) {
            System.err.println("Cannot load vital signs - patient is null");
            return;
        }
        
        System.out.println("Loading vital signs for patient: " + patient.getName());
        
        ArrayList<VitalSign> vitals = patient.viewPreviousVitals();
        
        // Update the ListView
        Platform.runLater(() -> {
            ObservableList<VitalSign> vitalsData = FXCollections.observableArrayList(vitals);
            vitalsListView.setItems(vitalsData);
            
            // Clear existing chart data
            heartRateSeries.getData().clear();
            systolicSeries.getData().clear();
            diastolicSeries.getData().clear();
            oxygenSeries.getData().clear();
            temperatureSeries.getData().clear();
            
            // Populate chart series with data
            for (VitalSign vital : vitals) {
                String dateTime = DateUtil.format(vital.getDateTimeRecorded());
                
                // Add data to chart series
                heartRateSeries.getData().add(new XYChart.Data<>(dateTime, vital.getHeartRate()));
                
                // Parse blood pressure (format: "120/80")
                String[] bpParts = vital.getBloodPressure().split("/");
                if (bpParts.length == 2) {
                    try {
                        int systolic = Integer.parseInt(bpParts[0]);
                        int diastolic = Integer.parseInt(bpParts[1]);
                        
                        systolicSeries.getData().add(new XYChart.Data<>(dateTime, systolic));
                        diastolicSeries.getData().add(new XYChart.Data<>(dateTime, diastolic));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing blood pressure: " + vital.getBloodPressure());
                    }
                }
                
                oxygenSeries.getData().add(new XYChart.Data<>(dateTime, vital.getOxygenLevel()));
                temperatureSeries.getData().add(new XYChart.Data<>(dateTime, vital.getTemperature()));
            }
            
            // Update summary cards with latest values
            updateVitalSummaryCards(vitals);
        });
        
        System.out.println("Loaded " + vitals.size() + " vital sign records.");
    }
    
    /**
     * Updates the summary cards with the latest vital signs
     * 
     * @param vitals List of vital signs to analyze
     */
    private void updateVitalSummaryCards(List<VitalSign> vitals) {
        if (vitals == null || vitals.isEmpty()) {
            resetSummaryCards();
            return;
        }
        
        // Find the most recent vital sign
        VitalSign latest = vitals.stream()
                .sorted((v1, v2) -> v2.getDateTimeRecorded().compareTo(v1.getDateTimeRecorded()))
                .findFirst()
                .orElse(null);
                
        if (latest == null) {
            resetSummaryCards();
            return;
        }
        
        // Update heart rate card
        heartRateLabel.setText(String.format("%.0f bpm", latest.getHeartRate()));
        updateStatusLabel(heartRateStatusLabel, 
                           latest.getHeartRate() >= MIN_HEART_RATE && 
                           latest.getHeartRate() <= MAX_HEART_RATE);
        
        // Update blood pressure card
        bpLabel.setText(latest.getBloodPressure() + " mmHg");
        // Parse blood pressure
        String[] bpParts = latest.getBloodPressure().split("/");
        boolean bpNormal = false;
        if (bpParts.length == 2) {
            try {
                int systolic = Integer.parseInt(bpParts[0]);
                int diastolic = Integer.parseInt(bpParts[1]);
                bpNormal = systolic >= MIN_SYSTOLIC && systolic <= MAX_SYSTOLIC && 
                           diastolic >= MIN_DIASTOLIC && diastolic <= MAX_DIASTOLIC;
            } catch (NumberFormatException e) {
                bpNormal = false;
            }
        }
        updateStatusLabel(bpStatusLabel, bpNormal);
        
        // Update oxygen level card
        o2Label.setText(String.format("%.0f%%", latest.getOxygenLevel()));
        updateStatusLabel(o2StatusLabel, 
                           latest.getOxygenLevel() >= MIN_OXYGEN && 
                           latest.getOxygenLevel() <= MAX_OXYGEN);
        
        // Update temperature card
        tempLabel.setText(String.format("%.1f°C", latest.getTemperature()));
        updateStatusLabel(tempStatusLabel, 
                           latest.getTemperature() >= MIN_TEMP && 
                           latest.getTemperature() <= MAX_TEMP);
    }
    
    /**
     * Updates a status label based on whether the value is normal or not
     * 
     * @param label The label to update
     * @param isNormal Whether the value is within normal range
     */
    private void updateStatusLabel(Label label, boolean isNormal) {
        if (isNormal) {
            label.setText("Normal");
            label.setStyle("-fx-text-fill: #27ae60;"); // Green color for normal
        } else {
            label.setText("Abnormal");
            label.setStyle("-fx-text-fill: #e74c3c;"); // Red color for abnormal
        }
    }
    
    /**
     * Resets the summary cards to default state
     */
    private void resetSummaryCards() {
        heartRateLabel.setText("-- bpm");
        bpLabel.setText("--/-- mmHg");
        o2Label.setText("-- %");
        tempLabel.setText("-- °C");
        
        heartRateStatusLabel.setText("No data");
        bpStatusLabel.setText("No data");
        o2StatusLabel.setText("No data");
        tempStatusLabel.setText("No data");
        
        String noDataStyle = "-fx-text-fill: #7f8c8d;"; // Gray color for no data
        heartRateStatusLabel.setStyle(noDataStyle);
        bpStatusLabel.setStyle(noDataStyle);
        o2StatusLabel.setStyle(noDataStyle);
        tempStatusLabel.setStyle(noDataStyle);
    }
    
    /**
     * Checks if any vital sign is outside the normal range
     * 
     * @param vital The vital sign to check
     * @return True if any vital is out of range, false otherwise
     */
    private boolean isAnyVitalOutOfRange(VitalSign vital) {
        if (vital == null) return false;
        
        // Check heart rate
        boolean heartRateNormal = vital.getHeartRate() >= MIN_HEART_RATE && 
                                 vital.getHeartRate() <= MAX_HEART_RATE;
        
        // Check blood pressure
        boolean bpNormal = true;
        String[] bpParts = vital.getBloodPressure().split("/");
        if (bpParts.length == 2) {
            try {
                int systolic = Integer.parseInt(bpParts[0]);
                int diastolic = Integer.parseInt(bpParts[1]);
                bpNormal = systolic >= MIN_SYSTOLIC && systolic <= MAX_SYSTOLIC && 
                          diastolic >= MIN_DIASTOLIC && diastolic <= MAX_DIASTOLIC;
            } catch (NumberFormatException e) {
                bpNormal = false;
            }
        }
        
        // Check oxygen level
        boolean oxygenNormal = vital.getOxygenLevel() >= MIN_OXYGEN && 
                              vital.getOxygenLevel() <= MAX_OXYGEN;
        
        // Check temperature
        boolean tempNormal = vital.getTemperature() >= MIN_TEMP && 
                            vital.getTemperature() <= MAX_TEMP;
        
        // Return true if any vital is out of range
        return !heartRateNormal || !bpNormal || !oxygenNormal || !tempNormal;
    }
    
    /**
     * Highlights a specific data point in the charts
     * 
     * @param vital The vital sign to highlight
     */
    private void highlightDataPoint(VitalSign vital) {
        // Implement highlighting for selected vital sign in charts
        String dateTime = DateUtil.format(vital.getDateTimeRecorded());
        
        // Find the corresponding data points in each chart
        for (XYChart.Series<String, Number> series : heartRateChart.getData()) {
            for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                if (dataPoint.getXValue().equals(dateTime)) {
                    // Apply highlight style
                    dataPoint.getNode().setStyle("-fx-background-color: #ff9800, white; -fx-background-radius: 5px; -fx-padding: 5px;");
                } else {
                    // Remove highlight
                    dataPoint.getNode().setStyle("");
                }
            }
        }
    }
    
    /**
     * Handles uploading vitals from CSV file
     */
    private void handleUploadVitals() {
        if (patient == null) {
            showAlert("Error", "Patient data not loaded properly", AlertType.ERROR);
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Vital Signs CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showOpenDialog(vitalsRoot.getScene().getWindow());
        if (file != null) {
            List<String> alerts = patient.uploadVitalsFromCSV(file.getAbsolutePath());
            showAlert("Vitals Uploaded", "Vitals uploaded successfully from: " + file.getName(), AlertType.INFORMATION);
            
            // Show alerts for critical vitals
            if (!alerts.isEmpty()) {
                String alertMessage = String.join("\n", alerts);
                showAlert("Critical Vitals Detected", alertMessage, AlertType.WARNING);
            }
            
            // Refresh the data
            loadVitalsData();
        }
    }
    
    /**
     * Handles adding a vital sign manually
     */
    private void handleAddVital() {
        if (patient == null) {
            showAlert("Error", "Patient data not loaded properly", AlertType.ERROR);
            return;
        }
        
        // Create dialog for manual vital sign entry
        Dialog<VitalSign> dialog = new Dialog<>();
        dialog.setTitle("Add Vital Sign");
        dialog.setHeaderText("Enter new vital sign values");
        
        // Set up the dialog buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create form fields
        TextField heartRateField = new TextField();
        heartRateField.setPromptText("Heart Rate (bpm)");
        
        TextField systolicField = new TextField();
        systolicField.setPromptText("Systolic (mmHg)");
        
        TextField diastolicField = new TextField();
        diastolicField.setPromptText("Diastolic (mmHg)");
        
        TextField oxygenField = new TextField();
        oxygenField.setPromptText("Oxygen Level (%)");
        
        TextField tempField = new TextField();
        tempField.setPromptText("Temperature (°C)");
        
        // Create form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        grid.add(new Label("Heart Rate (bpm):"), 0, 0);
        grid.add(heartRateField, 1, 0);
        grid.add(new Label("Blood Pressure:"), 0, 1);
        grid.add(systolicField, 1, 1);
        grid.add(new Label("/"), 2, 1);
        grid.add(diastolicField, 3, 1);
        grid.add(new Label("Oxygen Level (%):"), 0, 2);
        grid.add(oxygenField, 1, 2);
        grid.add(new Label("Temperature (°C):"), 0, 3);
        grid.add(tempField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert dialog result to vital sign
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    double heartRate = Double.parseDouble(heartRateField.getText());
                    int systolic = Integer.parseInt(systolicField.getText());
                    int diastolic = Integer.parseInt(diastolicField.getText());
                    String bloodPressure = systolic + "/" + diastolic;
                    double oxygenLevel = Double.parseDouble(oxygenField.getText());
                    double temperature = Double.parseDouble(tempField.getText());
                    
                    return new VitalSign(patient.getId(), heartRate, oxygenLevel, 
                                         bloodPressure, temperature, LocalDateTime.now());
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for all fields", AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<VitalSign> result = dialog.showAndWait();
        result.ifPresent(vital -> {
            // Add the vital sign to patient
            String alert = patient.uploadVitalSign(vital);
            loadVitalsData();
            
            // Show alert if critical
            if (alert != null && !alert.isEmpty()) {
                showAlert("Critical Vital Sign", alert, AlertType.WARNING);
            }
        });
    }
    
    /**
     * Handles removing the selected vital sign
     */
    private void handleRemoveVital() {
        VitalSign selected = vitalsListView.getSelectionModel().getSelectedItem();
        if (selected != null && patient != null) {
            Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Removal");
            confirmAlert.setHeaderText("Remove Vital Sign");
            confirmAlert.setContentText("Are you sure you want to remove this vital sign record?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                patient.removeVital(selected);
                loadVitalsData();
                showAlert("Vital Removed", "Vital sign record has been removed", AlertType.INFORMATION);
            }
        } else {
            showAlert("No Selection", "Please select a vital sign to remove", AlertType.WARNING);
        }
    }
    
    /**
     * Shows an alert dialog with the specified title, message, and type
     * 
     * @param title The title of the alert
     * @param message The message to display
     * @param type The alert type
     */
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows the vitals chart - can be called from PatientDashboardController
     * Makes sure the chart is visible and has data
     */
    public void showVitalsChart() {
        if (patient == null) {
            showAlert("Error", "Patient data not loaded properly", AlertType.ERROR);
            return;
        }
        
        // Refresh data if needed
        if (heartRateSeries.getData().isEmpty()) {
            loadVitalsData();
        }
        
        // Make sure the graphs are visible by selecting the first tab
        Platform.runLater(() -> {
            // Ensure the parent tabs select the vitals tab
            if (graphTabPane.getScene() != null && graphTabPane.getScene().getWindow() != null) {
                graphTabPane.getScene().getWindow().requestFocus();
                
                // Bring graph tab to front
                graphTabPane.getSelectionModel().select(0);
                
                // Add a visual highlight effect to show that graphs are active
                graphTabPane.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(66, 133, 244, 0.8), 10, 0, 0, 0);");
                
                // Remove the highlight effect after a delay
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        Platform.runLater(() -> {
                            graphTabPane.setStyle("");
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        
        // Log that the charts are being shown
        System.out.println("Showing vital sign charts for patient: " + patient.getName());
    }
}
