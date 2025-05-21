package com.rpms.GUI;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import com.rpms.AppointmentHandling.Appointment;
import com.rpms.ChatVideoConsultation.VideoCall;
import com.rpms.DoctorPatientInteraction.Feedback;
import com.rpms.DoctorPatientInteraction.Prescription;
import com.rpms.HealthData.VitalSign;
import com.rpms.Main;
import com.rpms.Reports.ReportGenerator;
import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Dashboard interface for doctor users.
 * Provides functionality to manage patients, view and approve appointments,
 * give medical feedback, and engage in video consultations.
 */
public class DoctorDashboard {

    /** The doctor user who is currently logged in */
    private Doctor doctor;

    /** Flag to track if reminders have been shown during this session */
    private boolean remindersShown = false; // for keeping track of alerts

    /**
     * Creates a new doctor dashboard for the specified doctor.
     * 
     * @param doctor The doctor user who logged in
     */
    public DoctorDashboard(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Initializes and displays the doctor dashboard interface.
     * Sets up all tabs, components, and event handlers.
     * 
     * @param stage The JavaFX stage to display the dashboard
     */
    public void start(Stage stage) {
        // Refresh patient data from Administrator's list to ensure fresh data
        ArrayList<Patient> refreshedPatients = new ArrayList<>();
        for (Patient p : doctor.getPatients()) {
            for (Patient adminPatient : Administrator.getPatients()) {
                if (p.getId().equals(adminPatient.getId())) {
                    refreshedPatients.add(adminPatient); // Use the version from Administrator
                    break;
                }
            }
        }
        
        // Set the refreshed list back to the doctor
        doctor.getPatients().clear();
        doctor.getPatients().addAll(refreshedPatients);

        // Now continue with the original code
        stage.setTitle("Doctor Dashboard - " + doctor.getName());

        // Create a border pane for main layout with vertical tabs
        BorderPane mainLayout = new BorderPane();

        // Create title bar at top
        Label titleLabel = new Label("Doctor Dashboard - Dr. " + doctor.getName());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        HBox header = new HBox(10, titleLabel);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("dashboard-header");
        
        mainLayout.setTop(header);

        // Create TabPane with VERTICAL tabs on the LEFT
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.LEFT);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("vertical-tab-pane");

        // === PATIENT MANAGEMENT TAB ===
        Tab patientTab = new Tab("Patients");
        patientTab.setClosable(false);
        // Add a graphic with icon to the tab
        VBox patientTabGraphic = new VBox(5);
        patientTabGraphic.setAlignment(Pos.CENTER);
        Label patientIcon = new Label("👨‍⚕️");
        patientIcon.setFont(Font.font("System", 20));
        Label patientText = new Label("Patients");
        patientTabGraphic.getChildren().addAll(patientIcon, patientText);
        patientTab.setGraphic(patientTabGraphic);
        
        // Content for patient tab
        VBox patientContainer = new VBox(10);
        patientContainer.setPadding(new Insets(10));

        for (Patient patient : doctor.getPatients()) {
            HBox patientBox = new HBox(10);
            patientBox.setPadding(new Insets(10));
            patientBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");
            Label nameLabel = new Label(patient.toString());

            Button viewVitalsBtn = new Button("View Vitals");
            viewVitalsBtn.setOnAction(e -> showVitalsPopup(patient));

            Button downloadReportBtn = new Button("Download Report");
            downloadReportBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Patient Report");
                fileChooser.setInitialFileName(patient.getName() + "_Report.txt");
                File file = fileChooser.showSaveDialog(null);
                if (file != null) {
                    ReportGenerator.generatePatientReport(patient, file);
                }
            });

            Button feedbackBtn = new Button("Give Feedback");
            feedbackBtn.setOnAction(e -> showFeedbackDialog(patient));

            Button graphBtn = new Button("View Vitals Graph");
            graphBtn.setOnAction(e -> patient.getVitals().generateVitalsGraph(new Stage()));
            
            // Add new chat button
            Button chatBtn = new Button("Chat");
            chatBtn.setOnAction(e -> {
                ChatWindow chatWindow = new ChatWindow(doctor, patient);
                chatWindow.show();
            });

            patientBox.getChildren().addAll(nameLabel, viewVitalsBtn, graphBtn, downloadReportBtn, feedbackBtn, chatBtn);
            patientContainer.getChildren().add(patientBox);
        }

        if (doctor.getPatients().isEmpty()) {
            patientContainer.getChildren().add(new Label("No patients assigned."));
        }

        ScrollPane patientScroll = new ScrollPane(patientContainer);
        patientScroll.setFitToWidth(true);
        patientTab.setContent(patientScroll);

        // === APPOINTMENTS & VIDEO CALLS TAB ===
        Tab appointmentsTab = new Tab("Appointments");
        appointmentsTab.setClosable(false);
        // Add a graphic with icon to the tab
        VBox appointmentTabGraphic = new VBox(5);
        appointmentTabGraphic.setAlignment(Pos.CENTER);
        Label appointmentIcon = new Label("📅");
        appointmentIcon.setFont(Font.font("System", 20));
        Label appointmentText = new Label("Appointments");
        appointmentTabGraphic.getChildren().addAll(appointmentIcon, appointmentText);
        appointmentsTab.setGraphic(appointmentTabGraphic);

        VBox tabContent = new VBox(20);
        tabContent.setPadding(new Insets(10));

        // Appointments Section
        VBox appointmentsBox = new VBox(10);
        appointmentsBox.getChildren().add(new Label("Pending/Upcoming Appointments:"));

        if (doctor.getAppointments().isEmpty()) {
            appointmentsBox.getChildren().add(new Label("No appointments available."));
        } else {
            for (Appointment appt : doctor.getPendingAndApprovedAppointments()) {
                HBox apptBox = new HBox(10);
                apptBox.setAlignment(Pos.CENTER_LEFT);
                apptBox.setStyle("-fx-border-color: lightgray; -fx-padding: 5;");
                Label info = new Label(appt.toString());

                Button approveBtn = new Button("Approve");
                approveBtn.setOnAction(e -> {
                    doctor.approveAppointment(appt);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment approved.");
                    alert.show();
                    start(stage); // refresh
                });

                Button cancelBtn = new Button("Reject");
                cancelBtn.setOnAction(e -> {
                    doctor.cancelAppointment(appt);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment cancelled.");
                    alert.show();
                    start(stage); // refresh
                });

                switch(appt.getStatus()) {
                    case "Cancelled":
                        approveBtn.setDisable(true);
                        cancelBtn.setDisable(true);
                        break;
                    case "Pending":
                        cancelBtn.setText("Reject");
                        break;
                    case "Approved":
                        approveBtn.setDisable(true);
                        cancelBtn.setText("Cancel");
                        break;
                }
                apptBox.getChildren().addAll(info, approveBtn, cancelBtn);
                appointmentsBox.getChildren().add(apptBox);
            }
        }

        ScrollPane apptScroll = new ScrollPane(appointmentsBox);
        apptScroll.setFitToWidth(true);

        // Video Call Section
        VBox videoCallBox = new VBox(10);
        videoCallBox.getChildren().add(new Label("Video Calls:"));

        if (doctor.getVideoCalls().isEmpty()) {
            videoCallBox.getChildren().add(new Label("No video calls available."));
        } else {
            for (VideoCall call : doctor.getVideoCalls()) {
                VBox callInfo = new VBox(5);
                callInfo.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
                Label details = new Label(call.toString());

                Button approveBtn = new Button("Approve");
                approveBtn.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog("https://meet.google.com/");
                    dialog.setTitle("Enter Google Meet Link");
                    dialog.setHeaderText("Approve Video Call");
                    dialog.setContentText("Enter the Google Meet link:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(link -> {
                        call.setLink(link);
                        doctor.approveVideoCall(call);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Video call approved with link:\n" + link);
                        alert.show();
                        start(stage); // refresh
                    });
                });

                Button cancelBtn = new Button("Reject");
                cancelBtn.setOnAction(e -> {
                    doctor.cancelVideoCall(call);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Video call cancelled.");
                    alert.show();
                    start(stage); // refresh
                });

                // Handle status-based UI
                switch (call.getStatus()) {
                    case "Pending":
                        cancelBtn.setText("Reject");
                        break;
                    case "Approved":
                        approveBtn.setDisable(true);
                        cancelBtn.setText("Cancel");
                        break;
                }

                callInfo.getChildren().addAll(details, approveBtn, cancelBtn);
                videoCallBox.getChildren().add(callInfo);
            }
        }

        ScrollPane videoScroll = new ScrollPane(videoCallBox);
        videoScroll.setFitToWidth(true);

        tabContent.getChildren().addAll(apptScroll, videoScroll);
        appointmentsTab.setContent(tabContent);
        
        // === CHAT TAB ===
        Tab chatTab = new Tab("Chat");
        chatTab.setClosable(false);
        // Add a graphic with icon to the tab
        VBox chatTabGraphic = new VBox(5);
        chatTabGraphic.setAlignment(Pos.CENTER);
        Label chatIcon = new Label("💬");
        chatIcon.setFont(Font.font("System", 20));
        Label chatText = new Label("Chat");
        chatTabGraphic.getChildren().addAll(chatIcon, chatText);
        chatTab.setGraphic(chatTabGraphic);
        
        VBox chatContainer = new VBox(10);
        chatContainer.setPadding(new Insets(10));
        
        Label chatHeaderLabel = new Label("Chat with your patients");
        chatHeaderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        chatContainer.getChildren().add(chatHeaderLabel);
        
        // List all patients with chat buttons
        for (Patient patient : doctor.getPatients()) {
            HBox patientChatRow = new HBox(10);
            patientChatRow.setAlignment(Pos.CENTER_LEFT);
            patientChatRow.setPadding(new Insets(5));
            patientChatRow.setStyle("-fx-border-color: #eee; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
            
            Label patientLabel = new Label(patient.getName() + " (ID: " + patient.getId() + ")");
            patientLabel.setPrefWidth(200);
            
            Button openChatBtn = new Button("Open Chat");
            openChatBtn.setOnAction(e -> {
                ChatWindow chatWindow = new ChatWindow(doctor, patient);
                chatWindow.show();
            });
            
            patientChatRow.getChildren().addAll(patientLabel, openChatBtn);
            chatContainer.getChildren().add(patientChatRow);
        }
        
        if (doctor.getPatients().isEmpty()) {
            chatContainer.getChildren().add(new Label("No patients to chat with."));
        }
        
        ScrollPane chatScroll = new ScrollPane(chatContainer);
        chatScroll.setFitToWidth(true);
        chatTab.setContent(chatScroll);

        // === Add Tabs to TabPane ===
        tabPane.getTabs().addAll(patientTab, appointmentsTab, chatTab);
        
        // Add TabPane to the center
        mainLayout.setCenter(tabPane);

        // Create Logout Button at bottom left
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> {
            stage.close(); // Close the current dashboard
            Main.logout(); // Call the logout method
        });
        
        // Create bottom panel for logout button
        HBox bottomBox = new HBox(logoutButton);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        mainLayout.setBottom(bottomBox);

        // Only show critical vitals alert if it hasn't been shown yet in this session
        if (!remindersShown) {
            String msg = doctor.patientCriticalVitalDetection();
            if (!msg.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, msg);
                alert.setTitle("Critical Vitals Detected");
                alert.setHeaderText("Critical Vitals Detected!");
                alert.showAndWait();
            }
            // Mark that reminders have been shown for this session
            remindersShown = true;
        }

        Scene scene = new Scene(mainLayout, 900, 600);
        
        // Add CSS stylesheet
        String cssPath = com.rpms.Main.getStylesheetPath();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }

        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);  // Force JVM shutdown
        });

        stage.show();
    }

    /**
     * Displays a popup window showing all vital signs for a patient.
     * 
     * @param patient The patient whose vital signs to display
     */
    private void showVitalsPopup(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vitals - " + patient.getName());
        alert.setHeaderText("Vitals History");

        StringBuilder vitalsText = new StringBuilder();
        ArrayList<VitalSign> vitals = patient.getVitals().getVitals();

        System.out.println("DEBUG: Attempting to show vitals for patient: " + patient.getName());
        System.out.println("DEBUG: Vitals list size: " + (vitals != null ? vitals.size() : "null"));

        if (vitals == null || vitals.isEmpty()) {
            vitalsText.append("No vitals recorded for this patient.");
        } else {
            for (VitalSign v : vitals) {
                vitalsText.append(v.toString()).append("\n");
            }
        }

        alert.setContentText(vitalsText.toString());
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(500, 300);
        alert.showAndWait();
    }

    /**
     * Displays a dialog for entering feedback and prescriptions for a patient.
     * 
     * @param patient The patient to provide feedback to
     */
    private void showFeedbackDialog(Patient patient) {
        Dialog<Feedback> dialog = new Dialog<>();
        dialog.setTitle("Give Feedback");

        Label commentLabel = new Label("Comments:");
        TextArea commentArea = new TextArea();

        VBox prescriptionBox = new VBox(5);
        Button addPrescriptionBtn = new Button("Add Prescription");

        ArrayList<Prescription> prescriptions = new ArrayList<>();

        addPrescriptionBtn.setOnAction(e -> {
            Dialog<Prescription> presDialog = new Dialog<>();
            presDialog.setTitle("Add Prescription");

            TextField medField = new TextField();
            TextField dosageField = new TextField();
            TextField scheduleField = new TextField();

            GridPane presGrid = new GridPane();
            presGrid.setHgap(10);
            presGrid.setVgap(10);
            presGrid.setPadding(new Insets(10));

            presGrid.add(new Label("Medication:"), 0, 0);
            presGrid.add(medField, 1, 0);
            presGrid.add(new Label("Dosage:"), 0, 1);
            presGrid.add(dosageField, 1, 1);
            presGrid.add(new Label("Schedule:"), 0, 2);
            presGrid.add(scheduleField, 1, 2);

            presDialog.getDialogPane().setContent(presGrid);
            presDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            presDialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    return new Prescription(medField.getText(), dosageField.getText(), scheduleField.getText());
                }
                return null;
            });

            Prescription prescription = presDialog.showAndWait().orElse(null);
            if (prescription != null) {
                prescriptions.add(prescription);
                prescriptionBox.getChildren().add(new Label(prescription.toString()));
            }
        });

        VBox content = new VBox(10, commentLabel, commentArea, addPrescriptionBtn, prescriptionBox);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new Feedback(commentArea.getText(), prescriptions, LocalDateTime.now());
            }
            return null;
        });

        Feedback feedback = dialog.showAndWait().orElse(null);
        if (feedback != null) {
            patient.addFeedback(feedback);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Feedback added.");
            alert.show();
        }
    }
}