package com.rpms.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rpms.AppointmentHandling.Appointment;
import com.rpms.AppointmentHandling.AppointmentManager;
import com.rpms.ChatVideoConsultation.ChatManager;
import com.rpms.ChatVideoConsultation.VideoCall;
import com.rpms.DoctorPatientInteraction.Feedback;
import com.rpms.GUI.ChatWindow;
import com.rpms.Main;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.Users.User;
import com.rpms.utilities.DataManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the doctor dashboard
 * Handles all functionality for doctor users
 */
public class DoctorDashboardController {
    
    // Reference to the current doctor
    private Doctor doctor;
    
    // FXML UI components
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // Patients tab
    @FXML private TabPane tabPane;
    @FXML private ListView<Patient> patientsListView;
    @FXML private Button viewPatientButton;
    
    // Appointments tab
    @FXML private ListView<Appointment> pendingAppointmentsListView;
    @FXML private ListView<Appointment> approvedAppointmentsListView;
    
    // Video calls tab
    @FXML private ListView<VideoCall> pendingVideoCallsListView;
    @FXML private ListView<VideoCall> scheduledVideoCallsListView;
    
    // Feedbacks tab
    @FXML private ListView<Patient> feedbacksPatientsListView;
    @FXML private Button provideFeedbackButton;
    
    // Chat tab
    @FXML private VBox chatLayout;
    
    /**
     * Initializes the controller
     */
    @FXML
    public void initialize() {
        // Set up button actions
        logoutButton.setOnAction(e -> handleLogout());
        viewPatientButton.setOnAction(e -> handleViewPatient());
        provideFeedbackButton.setOnAction(e -> handleProvideFeedback());
        
        System.out.println("DoctorDashboardController initialized successfully");
    }
    
    /**
     * Sets the doctor and initializes all data
     * @param doctor The doctor user
     */
    public void initializeData(Doctor doctor) {
        System.out.println("Initializing DoctorDashboardController with doctor: " + doctor.getName());
        this.doctor = doctor;
        welcomeLabel.setText("Welcome, Dr. " + doctor.getName());
        
        // Set up cell factories for all list views
        setupPatientsListView();
        setupAppointmentsListViews();
        setupVideoCallsListViews();
        
        // Load data for all tabs
        loadPatientsData();
        loadAppointmentsData();
        loadVideoCallsData();
        loadFeedbacksData();
        loadChatData();
        
        System.out.println("DoctorDashboardController initialization complete");
    }
    
    /**
     * Sets up the patients list view with custom cell factory
     */
    private void setupPatientsListView() {
        patientsListView.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getName() + " (ID: " + patient.getId() + ")");
                }
            }
        });
        
        feedbacksPatientsListView.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getName() + " (ID: " + patient.getId() + ")");
                }
            }
        });
    }
    
    /**
     * Sets up the appointments list views with custom cell factories
     */
    private void setupAppointmentsListViews() {
        // Pending appointments list with Approve/Reject buttons
        pendingAppointmentsListView.setCellFactory(lv -> new ListCell<Appointment>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox hbox = new HBox(10);
            private final Label label = new Label("");
            
            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(label, Priority.ALWAYS);
                label.setMaxWidth(Double.MAX_VALUE);
                hbox.getChildren().addAll(label, approveButton, rejectButton);
                
                approveButton.setOnAction(event -> {
                    Appointment appointment = getItem();
                    AppointmentManager.approveAppointment(appointment);
                    loadAppointmentsData(); // Refresh
                    showAlert("Appointment Approved", "Appointment with " + appointment.getPatient().getName() + " has been approved.", AlertType.INFORMATION);
                });
                
                rejectButton.setOnAction(event -> {
                    Appointment appointment = getItem();
                    AppointmentManager.cancelAppointment(appointment);
                    loadAppointmentsData(); // Refresh
                    showAlert("Appointment Rejected", "Appointment with " + appointment.getPatient().getName() + " has been rejected.", AlertType.INFORMATION);
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
        
        // Approved appointments list with Cancel button
        approvedAppointmentsListView.setCellFactory(lv -> new ListCell<Appointment>() {
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
                    AppointmentManager.cancelAppointment(appointment);
                    loadAppointmentsData(); // Refresh
                    showAlert("Appointment Cancelled", "Appointment with " + appointment.getPatient().getName() + " has been cancelled.", AlertType.INFORMATION);
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
    }
    
    /**
     * Sets up the video calls list views with custom cell factories
     */
    private void setupVideoCallsListViews() {
        // Pending video calls list with Approve/Reject buttons
        pendingVideoCallsListView.setCellFactory(lv -> new ListCell<VideoCall>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox hbox = new HBox(10);
            private final Label label = new Label("");
            
            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(label, Priority.ALWAYS);
                label.setMaxWidth(Double.MAX_VALUE);
                hbox.getChildren().addAll(label, approveButton, rejectButton);
                
                approveButton.setOnAction(event -> {
                    VideoCall videoCall = getItem();
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Meeting Link");
                    dialog.setHeaderText("Please enter the video call meeting link");
                    dialog.setContentText("Link:");
                    
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        videoCall.setMeetingLink(result.get());
                        AppointmentManager.approveVideoCall(videoCall);
                        loadVideoCallsData(); // Refresh
                        showAlert("Video Call Approved", "Video call with " + videoCall.getPatient().getName() + " has been approved.", AlertType.INFORMATION);
                    }
                });
                
                rejectButton.setOnAction(event -> {
                    VideoCall videoCall = getItem();
                    AppointmentManager.cancelVideoCall(videoCall);
                    loadVideoCallsData(); // Refresh
                    showAlert("Video Call Rejected", "Video call with " + videoCall.getPatient().getName() + " has been rejected.", AlertType.INFORMATION);
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
        
        // Scheduled video calls list with Cancel button
        scheduledVideoCallsListView.setCellFactory(lv -> new ListCell<VideoCall>() {
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
                    AppointmentManager.cancelVideoCall(videoCall);
                    loadVideoCallsData(); // Refresh
                    showAlert("Video Call Cancelled", "Video call with " + videoCall.getPatient().getName() + " has been cancelled.", AlertType.INFORMATION);
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
     * Loads patients data
     */
    private void loadPatientsData() {
        if (doctor != null && patientsListView != null) {
            List<Patient> patients = doctor.getPatients();
            patientsListView.setItems(FXCollections.observableArrayList(patients));
            feedbacksPatientsListView.setItems(FXCollections.observableArrayList(patients));
            System.out.println("Loaded " + patients.size() + " patients");
        }
    }
    
    /**
     * Loads appointments data
     */
    private void loadAppointmentsData() {
        if (doctor != null) {
            // Get all appointments for this doctor
            List<Appointment> allAppointments = AppointmentManager.getAppointments().stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.getId()))
                .collect(Collectors.toList());
            
            // Filter by status
            List<Appointment> pendingAppointments = allAppointments.stream()
                .filter(a -> "Pending".equals(a.getStatus()))
                .collect(Collectors.toList());
                
            List<Appointment> approvedAppointments = allAppointments.stream()
                .filter(a -> "Approved".equals(a.getStatus()))
                .collect(Collectors.toList());
            
            // Update UI
            pendingAppointmentsListView.setItems(FXCollections.observableArrayList(pendingAppointments));
            approvedAppointmentsListView.setItems(FXCollections.observableArrayList(approvedAppointments));
            
            System.out.println("Loaded " + pendingAppointments.size() + " pending appointments");
            System.out.println("Loaded " + approvedAppointments.size() + " approved appointments");
        }
    }
    
    /**
     * Loads video calls data
     */
    private void loadVideoCallsData() {
        if (doctor != null) {
            // Get all video calls for this doctor
            List<VideoCall> allVideoCalls = AppointmentManager.getVideoCalls().stream()
                .filter(vc -> vc.getDoctor().getId().equals(doctor.getId()))
                .collect(Collectors.toList());
            
            // Filter by status
            List<VideoCall> pendingVideoCalls = allVideoCalls.stream()
                .filter(vc -> "Pending".equals(vc.getStatus()))
                .collect(Collectors.toList());
                
            List<VideoCall> scheduledVideoCalls = allVideoCalls.stream()
                .filter(vc -> "Approved".equals(vc.getStatus()))
                .collect(Collectors.toList());
            
            // Update UI
            pendingVideoCallsListView.setItems(FXCollections.observableArrayList(pendingVideoCalls));
            scheduledVideoCallsListView.setItems(FXCollections.observableArrayList(scheduledVideoCalls));
            
            System.out.println("Loaded " + pendingVideoCalls.size() + " pending video calls");
            System.out.println("Loaded " + scheduledVideoCalls.size() + " scheduled video calls");
        }
    }
    
    /**
     * Loads feedbacks data (patients who need feedback)
     */
    private void loadFeedbacksData() {
        // This controller already loads patients into the feedbacksPatientsListView
        // in the loadPatientsData method
    }
    
    /**
     * Loads chat data
     */
    private void loadChatData() {
        chatLayout.getChildren().clear();
        
        Label chatLabel = new Label("Chat with your patients");
        chatLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        chatLayout.getChildren().add(chatLabel);
        
        // Get all patients this doctor can chat with
        List<User> availablePatients = ChatManager.getChatContactsForUser(doctor);
        
        if (!availablePatients.isEmpty()) {
            for (User user : availablePatients) {
                if (user instanceof Patient) {
                    Patient patient = (Patient) user;
                    HBox patientRow = new HBox(10);
                    patientRow.setAlignment(Pos.CENTER_LEFT);
                    patientRow.setPadding(new Insets(5));
                    patientRow.setStyle("-fx-border-color: #eee; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
                    
                    Label patientLabel = new Label(patient.getName());
                    patientLabel.setPrefWidth(200);
                    
                    Button openChatBtn = new Button("Open Chat");
                    openChatBtn.setOnAction(e -> {
                        ChatWindow chatWindow = new ChatWindow(doctor, patient);
                        chatWindow.show();
                    });
                    
                    patientRow.getChildren().addAll(patientLabel, openChatBtn);
                    chatLayout.getChildren().add(patientRow);
                }
            }
        } else {
            chatLayout.getChildren().add(new Label("You don't have any patients to message."));
        }
    }
    
    /**
     * Handles patient view action
     */
    @FXML
    private void handleViewPatient() {
        Patient selectedPatient = patientsListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("No Selection", "Please select a patient to view", AlertType.WARNING);
            return;
        }
        
        // Create a dialog to display patient details
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Patient Details");
        dialog.setHeaderText("Details for " + selectedPatient.getName());
        
        // Create a grid for the patient details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Add patient details
        grid.add(new Label("ID:"), 0, 0);
        grid.add(new Label(selectedPatient.getId()), 1, 0);
        
        grid.add(new Label("Name:"), 0, 1);
        grid.add(new Label(selectedPatient.getName()), 1, 1);
        
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(new Label(selectedPatient.getPhoneNumber()), 1, 2);
        
        grid.add(new Label("Email:"), 0, 3);
        grid.add(new Label(selectedPatient.getEmail()), 1, 3);
        
        grid.add(new Label("Emergency Contacts:"), 0, 4);
        grid.add(new Label(String.join(", ", selectedPatient.getEmergencyContacts())), 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }
    
    /**
     * Handles providing feedback to a patient
     */
    @FXML
    private void handleProvideFeedback() {
        Patient selectedPatient = feedbacksPatientsListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("No Selection", "Please select a patient to provide feedback", AlertType.WARNING);
            return;
        }
        
        // Create a dialog for entering feedback
        Dialog<Feedback> dialog = new Dialog<>();
        dialog.setTitle("Provide Feedback");
        dialog.setHeaderText("Enter feedback for " + selectedPatient.getName());
        
        // Set the button types
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        
        // Create the username and password labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Feedback Title");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Feedback Content");
        contentArea.setPrefRowCount(6);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentArea, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result to a Feedback object when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String title = titleField.getText();
                String content = contentArea.getText();
                
                if (title.isEmpty() || content.isEmpty()) {
                    showAlert("Invalid Input", "Title and content cannot be empty", AlertType.ERROR);
                    return null;
                }
                
                // Create feedback with the correct constructor parameters
                // The constructor requires: String, ArrayList<Prescription>, LocalDateTime
                return new Feedback(content, new java.util.ArrayList<>(), java.time.LocalDateTime.now());
            }
            return null;
        });
        
        Optional<Feedback> result = dialog.showAndWait();
        
        result.ifPresent(feedback -> {
            // Save feedback to patient's records
            selectedPatient.addFeedback(feedback);
            feedback.setDoctor(doctor);
            showAlert("Feedback Provided", "Your feedback has been sent to " + selectedPatient.getName(), AlertType.INFORMATION);
            DataManager.saveAllData();
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
            showAlert("Error", "Error during logout: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Shows an alert dialog
     * @param title Alert title
     * @param message Alert message
     * @param type Alert type
     */
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
