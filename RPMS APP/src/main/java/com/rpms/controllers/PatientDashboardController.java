package com.rpms.controllers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.rpms.AppointmentHandling.Appointment;
import com.rpms.ChatVideoConsultation.ChatManager;
import com.rpms.ChatVideoConsultation.VideoCall;
import com.rpms.DoctorPatientInteraction.Feedback;
import com.rpms.GUI.ChatWindow;
import com.rpms.HealthData.VitalSign;
import com.rpms.Main;
import com.rpms.NotificationsAndReminders.ReminderService;
import com.rpms.Reports.ReportGenerator;
import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.Users.User;
import com.rpms.controllers.tabs.VitalsTabController;
import com.rpms.utilities.DataManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for the patient dashboard
 * Handles all functionality for patient users
 */
public class PatientDashboardController {
    
    // Reference to the current patient
    private Patient patient;
    
    // Flag to track if reminders have been shown
    private boolean remindersShown = false;
    
    // FXML UI components
    @FXML private Label welcomeLabel;
    @FXML private Button panicButton;
    @FXML private Button logoutButton;
    
    // Vitals tab
    @FXML private TabPane tabPane;
    @FXML private BorderPane vitalsTab;
    @FXML private VitalsTabController vitalsTabController;
    @FXML private Button uploadVitalsButton;
    @FXML private Button graphVitalsButton;
    @FXML private ListView<VitalSign> vitalsListView;
    
    // Appointments tab
    @FXML private ListView<Appointment> appointmentsListView;
    @FXML private Button requestAppointmentButton;
    @FXML private Button requestVideoCallButton;
    @FXML private ListView<VideoCall> videoCallsListView;
    
    // Emergency contacts tab
    @FXML private ListView<String> contactsListView;
    @FXML private Button addContactButton;
    
    // Feedbacks tab
    @FXML private ListView<Feedback> feedbacksListView;
    @FXML private Button downloadReportButton;
    
    // Chat tab
    @FXML private VBox chatLayout;
    
    /**
     * Initializes the controller
     */
    @FXML
    public void initialize() {
        // Button actions will be set up here once we have the patient
        panicButton.setOnAction(e -> handlePanicButton());
        logoutButton.setOnAction(e -> handleLogout());
        
        uploadVitalsButton.setOnAction(e -> handleUploadVitals());
        graphVitalsButton.setOnAction(e -> handleGraphVitals());
        
        requestAppointmentButton.setOnAction(e -> handleRequestAppointment());
        requestVideoCallButton.setOnAction(e -> handleRequestVideoCall());
        
        addContactButton.setOnAction(e -> handleAddContact());
        
        downloadReportButton.setOnAction(e -> handleDownloadReport());
        
        // Style the panic button prominently
        panicButton.getStyleClass().add("panic-button");
        
        System.out.println("PatientDashboardController initialized successfully");
    }
    
    /**
     * Sets the patient and initializes all data
     * @param patient The patient user
     */
    public void initializeData(Patient patient) {
        System.out.println("Initializing PatientDashboardController with patient: " + patient.getName());
        this.patient = patient;
        welcomeLabel.setText("Welcome, " + patient.getName());
        
        // Debugging UI components
        System.out.println("Debug UI Components:");
        System.out.println("- welcomeLabel: " + (welcomeLabel != null ? "found" : "null"));
        System.out.println("- tabPane: " + (tabPane != null ? "found" : "null"));
        System.out.println("- vitalsListView: " + (vitalsListView != null ? "found" : "null"));
        System.out.println("- appointmentsListView: " + (appointmentsListView != null ? "found" : "null"));
        System.out.println("- contactsListView: " + (contactsListView != null ? "found" : "null"));
        System.out.println("- feedbacksListView: " + (feedbacksListView != null ? "found" : "null"));
        System.out.println("- chatLayout: " + (chatLayout != null ? "found" : "null"));
        
        // Only initialize vitals tab controller if it exists
        if (vitalsTabController != null) {
            System.out.println("Setting patient for vitals tab controller");
            vitalsTabController.setPatient(patient);
        } else {
            System.out.println("VitalsTabController is null - using direct loading method instead");
            // Fall back to direct loading
            loadVitalsData();
        }
        
        // Load data for all tabs
        try {
            loadAppointmentsData();
            System.out.println("Appointments data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading appointments data: " + e.getMessage());
        }
        
        try {
            loadEmergencyContactsData();
            System.out.println("Emergency contacts data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading emergency contacts data: " + e.getMessage());
        }
        
        try {
            loadFeedbacksData();
            System.out.println("Feedbacks data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading feedbacks data: " + e.getMessage());
        }
        
        try {
            loadChatData();
            System.out.println("Chat data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading chat data: " + e.getMessage());
        }
        
        // Show reminders
        showReminders();
        
        System.out.println("PatientDashboardController initialization complete");
    }
    
    /**
     * Loads vital signs data directly
     * This is a fallback if the vitalsTabController isn't available
     */
    private void loadVitalsData() {
        System.out.println("Loading vitals data...");
        if (patient != null && vitalsListView != null) {
            ObservableList<VitalSign> vitalsData = FXCollections.observableArrayList(patient.viewPreviousVitals());
            vitalsListView.setItems(vitalsData);
            System.out.println("Loaded " + vitalsData.size() + " vital records");
        } else {
            System.err.println("Cannot load vitals: " + 
                              (patient == null ? "patient is null" : "vitalsListView is null"));
        }
    }
    
    /**
     * Loads appointments data
     */
    private void loadAppointmentsData() {
        // Clear and update appointments list
        ObservableList<Appointment> appointmentsData = FXCollections.observableArrayList(patient.viewAppointments());
        appointmentsListView.setItems(appointmentsData);
        
        // Custom cell factory to add cancel buttons
        appointmentsListView.setCellFactory(lv -> new ListCell<Appointment>() {
            private final Button cancelButton = new Button("Cancel");
            private final HBox hbox = new HBox(10);
            private final Label label = new Label("");
            
            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(label, Priority.ALWAYS);
                label.setMaxWidth(Double.MAX_VALUE);
                hbox.getChildren().addAll(label, cancelButton);
                
                cancelButton.setOnAction(event -> {
                    Appointment appointment = getItem();
                    boolean removed = patient.cancelAppointment(appointment);
                    loadAppointmentsData(); // Refresh
                    showAlert("Appointment", removed ? "Cancelled" : "Failed to cancel");
                });
            }
            
            @Override
            protected void updateItem(Appointment appointment, boolean empty) {
                super.updateItem(appointment, empty);
                if (empty || appointment == null) {
                    setGraphic(null);
                } else {
                    label.setText(appointment.toString());
                    setGraphic(hbox);
                }
            }
        });
        
        // Clear and update video calls list
        ObservableList<VideoCall> videoCallsData = FXCollections.observableArrayList(patient.viewVideoCalls());
        videoCallsListView.setItems(videoCallsData);
        
        // Custom cell factory to add cancel buttons for video calls
        videoCallsListView.setCellFactory(lv -> new ListCell<VideoCall>() {
            private final Button cancelButton = new Button("Cancel");
            private final HBox hbox = new HBox(10);
            private final Label label = new Label("");
            
            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(label, Priority.ALWAYS);
                label.setMaxWidth(Double.MAX_VALUE);
                hbox.getChildren().addAll(label, cancelButton);
                
                cancelButton.setOnAction(event -> {
                    VideoCall videoCall = getItem();
                    boolean removed = patient.cancelVideoCall(videoCall);
                    loadAppointmentsData(); // Refresh
                    showAlert("Video Call", removed ? "Cancelled" : "Failed to cancel");
                });
            }
            
            @Override
            protected void updateItem(VideoCall videoCall, boolean empty) {
                super.updateItem(videoCall, empty);
                if (empty || videoCall == null) {
                    setGraphic(null);
                } else {
                    label.setText(videoCall.toString());
                    setGraphic(hbox);
                }
            }
        });
    }
    
    /**
     * Loads emergency contacts data
     */
    private void loadEmergencyContactsData() {
        // Clear and update contacts list
        ObservableList<String> contactsData = FXCollections.observableArrayList(patient.getEmergencyContacts());
        contactsListView.setItems(contactsData);
        
        // Custom cell factory to add remove buttons
        contactsListView.setCellFactory(lv -> new ListCell<String>() {
            private final Button removeButton = new Button("Remove");
            private final HBox hbox = new HBox(10);
            private final Label label = new Label("");
            
            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(label, Priority.ALWAYS);
                label.setMaxWidth(Double.MAX_VALUE);
                hbox.getChildren().addAll(label, removeButton);
                
                removeButton.setOnAction(event -> {
                    String contact = getItem();
                    patient.removeEmergencyContact(contact);
                    loadEmergencyContactsData(); // Refresh
                    showAlert("Contact Removed", contact);
                });
            }
            
            @Override
            protected void updateItem(String contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setGraphic(null);
                } else {
                    label.setText(contact);
                    setGraphic(hbox);
                }
            }
        });
    }
    
    /**
     * Loads feedbacks data
     */
    private void loadFeedbacksData() {
        // Clear and update feedbacks list
        ObservableList<Feedback> feedbacksData = FXCollections.observableArrayList(patient.viewPreviousFeedbacks());
        feedbacksListView.setItems(feedbacksData);
    }
    
    /**
     * Loads chat data
     */
    private void loadChatData() {
        chatLayout.getChildren().clear();
        
        Label chatLabel = new Label("Chat with your doctors");
        chatLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        chatLayout.getChildren().add(chatLabel);
        
        // Get all doctors this patient can chat with
        List<User> availableDoctors = ChatManager.getChatContactsForUser(patient);
        
        if (!availableDoctors.isEmpty()) {
            // Create a section for the primary physician
            Doctor physician = patient.getPhysician();
            if (physician != null) {
                HBox primaryDocRow = new HBox(10);
                primaryDocRow.setAlignment(Pos.CENTER_LEFT);
                primaryDocRow.setPadding(new Insets(5));
                primaryDocRow.setStyle("-fx-border-color: #2a9d8f; -fx-border-radius: 5; -fx-background-color: #e9f5f3;");
                
                Label primaryLabel = new Label("Primary Physician:");
                primaryLabel.setStyle("-fx-font-weight: bold;");
                
                Label doctorLabel = new Label("Dr. " + physician.getName());
                doctorLabel.setPrefWidth(200);
                
                Button openChatBtn = new Button("Open Chat");
                openChatBtn.setOnAction(e -> {
                    ChatWindow chatWindow = new ChatWindow(patient, physician);
                    chatWindow.show();
                });
                
                primaryDocRow.getChildren().addAll(primaryLabel, doctorLabel, openChatBtn);
                chatLayout.getChildren().add(primaryDocRow);
            }
            
            // Add a separator and header for other doctors
            if (availableDoctors.size() > 1) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(5, 0, 5, 0));
                
                Label otherDoctorsLabel = new Label("Other Available Doctors:");
                otherDoctorsLabel.setStyle("-fx-font-weight: bold;");
                
                chatLayout.getChildren().addAll(separator, otherDoctorsLabel);
                
                // List all other doctors
                for (User user : availableDoctors) {
                    if (user instanceof Doctor doctor && (physician == null || !doctor.equals(physician))) {
                        HBox docRow = new HBox(10);
                        docRow.setAlignment(Pos.CENTER_LEFT);
                        docRow.setPadding(new Insets(5));
                        docRow.setStyle("-fx-border-color: #eee; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
                        
                        Label docNameLabel = new Label("Dr. " + doctor.getName());
                        docNameLabel.setPrefWidth(200);
                        
                        Button chatBtn = new Button("Open Chat");
                        chatBtn.setOnAction(e -> {
                            ChatWindow chatWindow = new ChatWindow(patient, doctor);
                            chatWindow.show();
                        });
                        
                        docRow.getChildren().addAll(docNameLabel, chatBtn);
                        chatLayout.getChildren().add(docRow);
                    }
                }
            }
        } else {
            chatLayout.getChildren().add(new Label("You don't have any doctors available for chat."));
        }
    }
    
    /**
     * Shows reminders for the patient
     */
    private void showReminders() {
        if (!remindersShown) {
            ReminderService reminderService = new ReminderService(patient);
            String appointments = reminderService.getAppointmentReminders();
            String medications = reminderService.getMedicationReminders();
            String videoCalls = reminderService.getApprovedVideoCalls();
            StringBuilder reminderMsg = new StringBuilder();
            if (!appointments.isEmpty()) reminderMsg.append("Upcoming Appointments:\n").append(appointments).append("\n");
            if (!medications.isEmpty()) reminderMsg.append("Medications:\n").append(medications).append("\n");
            if (!videoCalls.isEmpty()) reminderMsg.append("Upcoming Video Calls:\n").append(videoCalls);
            if (reminderMsg.length() > 0) {
                Platform.runLater(() -> showAlert("Reminders", reminderMsg.toString()));
            }
            remindersShown = true;
        }
    }
    
    /**
     * Handles the panic button press
     */
    @FXML
    private void handlePanicButton() {
        // Create a more attention-grabbing emergency dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("EMERGENCY ALERT");
        dialog.setHeaderText("What is your emergency?");
        
        // Set the button types
        ButtonType sendButtonType = new ButtonType("SEND ALERT", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);
        
        // Create and add the emergency message field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Describe your emergency situation");
        messageArea.setPrefRowCount(5);
        
        grid.add(new Label("Emergency Message:"), 0, 0);
        grid.add(messageArea, 0, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Style the dialog
        dialog.getDialogPane().getStyleClass().add("emergency-dialog");
        
        // Request focus on the text field by default
        Platform.runLater(() -> messageArea.requestFocus());
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                return messageArea.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(message -> {
            if (!message.trim().isEmpty()) {
                patient.panicButton(message);
                showAlert("Emergency Alert Sent", 
                          "Your emergency alert has been sent to your physician and emergency contacts.", 
                          Alert.AlertType.INFORMATION);
            }
        });
    }
    
    /**
     * Handles logout
     */
    @FXML
    private void handleLogout() {
        try {
            // Save data before logging out
            DataManager.saveAllData();
            
            // Get the current stage
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
            
            // Show login screen
            Main.logout();
        } catch (Exception e) {
            showAlert("Error", "Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles uploading vitals from CSV
     */
    @FXML
    private void handleUploadVitals() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Vital Signs CSV");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            List<String> alerts = patient.uploadVitalsFromCSV(file.getAbsolutePath());
            showAlert("Vitals Uploaded", "Vitals uploaded from: " + file.getName());
            if (!alerts.isEmpty()) {
                showAlert("Critical Vitals Detected", String.join("\n", alerts));
            }
            loadVitalsData(); // Refresh vitals list
        }
    }
    
    /**
     * Handles showing vital signs graph
     */
    @FXML
    private void handleGraphVitals() {
        if (patient != null) {
            // Directly use the patient's method to generate the graph
            patient.getVitals().generateVitalsGraph(new Stage());
        }
    }
    
    /**
     * Handles requesting a new appointment
     */
    @FXML
    private void handleRequestAppointment() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        TextInputDialog dateDialog = new TextInputDialog(LocalDateTime.now().plusDays(1).format(formatter));
        dateDialog.setTitle("Request Appointment");
        dateDialog.setHeaderText("Enter Appointment Date & Time (e.g. 2025-05-10T15:30)");
        
        Optional<String> dateResult = dateDialog.showAndWait();
        if (dateResult.isPresent()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateResult.get());
                List<Doctor> doctors = Administrator.getDoctors();
                
                if (doctors.isEmpty()) {
                    showAlert("Error", "No doctors available in the system");
                    return;
                }
                
                ChoiceDialog<Doctor> doctorDialog = new ChoiceDialog<>(doctors.get(0), doctors);
                doctorDialog.setTitle("Select Doctor");
                doctorDialog.setHeaderText("Choose a doctor for the appointment");
                
                Optional<Doctor> doctorResult = doctorDialog.showAndWait();
                if (doctorResult.isPresent()) {
                    Doctor selectedDoctor = doctorResult.get();
                    Appointment appt = new Appointment(dateTime, selectedDoctor, patient);
                    patient.requestAppointment(appt);
                    showAlert("Appointment Requested", appt.toString());
                    loadAppointmentsData(); // Refresh appointment list
                }
            } catch (Exception ex) {
                showAlert("Invalid Input", "Invalid date-time format: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Handles requesting a video call
     */
    @FXML
    private void handleRequestVideoCall() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        TextInputDialog startDialog = new TextInputDialog(LocalDateTime.now().plusDays(1).format(formatter));
        startDialog.setTitle("Request Video Call");
        startDialog.setHeaderText("Enter Start Date & Time (e.g. 2025-05-10T15:30)");
        
        Optional<String> startResult = startDialog.showAndWait();
        if (startResult.isPresent()) {
            try {
                LocalDateTime startTime = LocalDateTime.parse(startResult.get());
                
                TextInputDialog endDialog = new TextInputDialog(
                        LocalDateTime.parse(startResult.get()).plusHours(1).format(formatter));
                endDialog.setTitle("Request Video Call");
                endDialog.setHeaderText("Enter End Date & Time (e.g. 2025-05-10T16:30)");
                
                Optional<String> endResult = endDialog.showAndWait();
                if (endResult.isPresent()) {
                    try {
                        LocalDateTime endTime = LocalDateTime.parse(endResult.get());
                        
                        List<Doctor> doctors = Administrator.getDoctors();
                        if (doctors.isEmpty()) {
                            showAlert("Error", "No doctors available in the system");
                            return;
                        }
                        
                        ChoiceDialog<Doctor> doctorDialog = new ChoiceDialog<>(doctors.get(0), doctors);
                        doctorDialog.setTitle("Select Doctor");
                        doctorDialog.setHeaderText("Choose a doctor for the video call");
                        
                        Optional<Doctor> doctorResult = doctorDialog.showAndWait();
                        if (doctorResult.isPresent()) {
                            Doctor selectedDoctor = doctorResult.get();
                            VideoCall videoCall = new VideoCall(selectedDoctor, patient, startTime, endTime);
                            patient.requestVideoCall(videoCall);
                            showAlert("Video Call Requested", 
                                    "Video call scheduled with " + selectedDoctor.getName());
                            loadAppointmentsData(); // Refresh video calls list
                        }
                    } catch (Exception ex) {
                        showAlert("Invalid Input", "Invalid end date-time format: " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                showAlert("Invalid Input", "Invalid start date-time format: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Handles adding an emergency contact
     */
    @FXML
    private void handleAddContact() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter contact number to add");
        dialog.showAndWait().ifPresent(contact -> {
            patient.addEmergencyContact(contact);
            showAlert("Contact Added", contact);
            loadEmergencyContactsData(); // Refresh contacts list
        });
    }
    
    /**
     * Handles downloading a patient report
     */
    @FXML
    private void handleDownloadReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Patient Report");
        fileChooser.setInitialFileName("Patient_" + patient.getId() + "_Report.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            ReportGenerator.generatePatientReport(patient, selectedFile);
            showAlert("Report Saved", selectedFile.getAbsolutePath());
        }
    }
    
    /**
     * Shows an alert dialog
     * @param title Alert title
     * @param message Alert message
     */
    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
    
    /**
     * Shows an alert dialog with specified alert type
     * @param title Alert title
     * @param message Alert message
     * @param alertType Type of alert to show
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
