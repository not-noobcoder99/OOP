package com.rpms.GUI;

import java.util.ArrayList;

import com.rpms.Main;
import com.rpms.Users.Administrator;
import com.rpms.Users.Doctor;
import com.rpms.Users.Patient;
import com.rpms.utilities.ChatManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminDashboard extends Application {

    @Override
    public void start(Stage primaryStage) {
        // title
        primaryStage.setTitle("Administrator Dashboard");

        // create tabpane with VERTICAL tabs on the left
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.LEFT);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("vertical-tab-pane");

        // create tabs for doctors, patients, admins
        Tab doctorsTab = new Tab("Doctors", createDoctorsTab());
        doctorsTab.setClosable(false);
        doctorsTab.setGraphic(createTabGraphic("Doctors", "👨‍⚕️"));
        
        Tab patientsTab = new Tab("Patients", createPatientsTab());
        patientsTab.setClosable(false);
        patientsTab.setGraphic(createTabGraphic("Patients", "🏥"));
        
        Tab adminsTab = new Tab("Administrators", createAdminTab());
        adminsTab.setClosable(false);
        adminsTab.setGraphic(createTabGraphic("Admins", "👤"));
        
        // Create and add the System tab
        Tab systemTab = new Tab("System", createSystemTab());
        systemTab.setClosable(false);
        systemTab.setGraphic(createTabGraphic("System", "⚙️"));

        // add tabs to tabpane
        tabPane.getTabs().addAll(doctorsTab, patientsTab, adminsTab, systemTab);

        // Create Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> {
            primaryStage.close(); // Close the current dashboard
            Main.logout(); // Call the logout method
        });
         
        // Add title and logout button to header
        Label titleLabel = new Label("Administrator Dashboard");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        HBox header = new HBox(10, titleLabel);
        header.setPadding(new Insets(10));
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        
        HBox controlsBox = new HBox(10, logoutButton);
        controlsBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        BorderPane headerPane = new BorderPane();
        headerPane.setLeft(header);
        headerPane.setRight(controlsBox);
        headerPane.setPadding(new Insets(10));
        headerPane.getStyleClass().add("header-pane");

        // Use BorderPane for the overall layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(headerPane);
        mainLayout.setCenter(tabPane);
        mainLayout.setPadding(new Insets(0));
        
        // set up the scene
        Scene scene = new Scene(mainLayout, 900, 650);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);  // Force JVM shutdown
        });
        
        // Add CSS
        String cssPath = com.rpms.Main.getStylesheetPath();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }
        
        primaryStage.show();
    }
    
    /**
     * Creates a graphic for a tab with an icon and text
     */
    private VBox createTabGraphic(String text, String icon) {
        VBox box = new VBox(5);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 20));
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("System", 12));
        
        box.getChildren().addAll(iconLabel, textLabel);
        return box;
    }

    private VBox createDoctorsTab() {
        // create a table to display doctors
        TableView<Doctor> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // set up the table columns
        table.setItems(FXCollections.observableArrayList(Administrator.getDoctors()));

        // create columns for ID, name, and remove button
        TableColumn<Doctor, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        // create update and remove buttons for each doctor
        TableColumn<Doctor, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");
            private final Button removeBtn = new Button("Remove");
            private final HBox buttonsBox = new HBox(5, updateBtn, removeBtn);
            
            {
                buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
                
                updateBtn.setOnAction(e -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    showUpdateDoctorDialog(doctor, table);
                });
                
                removeBtn.setOnAction(e -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    // Confirm before removing
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                                            "Are you sure you want to remove Dr. " + doctor.getName() + "?",
                                            ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirm Removal");
                    confirm.showAndWait().ifPresent(type -> {
                        if (type == ButtonType.YES) {
                            Administrator.removeDoctor(doctor);
                            Administrator.addSystemLog("Removed doctor: " + doctor.getName());
                            table.setItems(javafx.collections.FXCollections.observableArrayList(Administrator.getDoctors()));
                        }
                    });
                });
            }
            
            @Override 
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });

        // add columns to the table
        java.util.Collections.addAll(table.getColumns(), idCol, nameCol, actionsCol);

        // Registration form
        Label formTitle = new Label("Register New Doctor");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(15));
        
        TextField nameField = new TextField(); 
        nameField.setPromptText("Name");
        TextField phoneField = new TextField(); 
        phoneField.setPromptText("Phone");
        TextField emailField = new TextField(); 
        emailField.setPromptText("Email");
        TextField usernameField = new TextField(); 
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField(); 
        passwordField.setPromptText("Password");
        
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Phone:"), 0, 1);
        form.add(phoneField, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(new Label("Username:"), 0, 3);
        form.add(usernameField, 1, 3);
        form.add(new Label("Password:"), 0, 4);
        form.add(passwordField, 1, 4);

        Button addBtn = new Button("Register Doctor");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> {
            // Validate fields
            if (nameField.getText().isEmpty() || usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert("Name, username and password are required fields.");
                return;
            }
            
            Doctor doctor = new Doctor("doc" + (Administrator.getDoctors().size() + 1),
                    nameField.getText(), phoneField.getText(), emailField.getText(),
                    usernameField.getText(), passwordField.getText());
            Administrator.registerDoctor(doctor);
            Administrator.addSystemLog("Registered doctor: " + doctor.getName());
            table.setItems(javafx.collections.FXCollections.observableArrayList(Administrator.getDoctors()));
            nameField.clear(); phoneField.clear(); emailField.clear(); usernameField.clear(); passwordField.clear();
        });

        form.add(addBtn, 1, 5);
        
        VBox formBox = new VBox(10, formTitle, form);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        
        Label tableTitle = new Label("Manage Doctors");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        layout.getChildren().addAll(tableTitle, table, formBox);
        return layout;
    }

    private VBox createPatientsTab() {
        // Create table for patients
        TableView<Patient> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(Administrator.getPatients()));

        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Patient, String> physicianCol = new TableColumn<>("Physician");
        physicianCol.setCellValueFactory(cellData -> {
            Doctor physician = cellData.getValue().getPhysician();
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> physician != null ? physician.getName() : "None"
            );
        });
        physicianCol.setPrefWidth(150);

        // Actions column with update and remove buttons
        TableColumn<Patient, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");
            private final Button removeBtn = new Button("Remove");
            private final HBox buttonsBox = new HBox(5, updateBtn, removeBtn);
            
            {
                buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
                
                updateBtn.setOnAction(e -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    showUpdatePatientDialog(patient, table);
                });
                
                removeBtn.setOnAction(e -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    // Confirm before removing
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                                            "Are you sure you want to remove patient " + patient.getName() + "?",
                                            ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirm Removal");
                    confirm.showAndWait().ifPresent(type -> {
                        if (type == ButtonType.YES) {
                            Administrator.removePatient(patient);
                            Administrator.addSystemLog("Removed patient: " + patient.getName());
                            table.setItems(FXCollections.observableArrayList(Administrator.getPatients()));
                        }
                    });
                });
            }
            
            @Override 
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });

        java.util.Collections.addAll(table.getColumns(), idCol, nameCol, physicianCol, actionsCol);

        // Registration form for new patients
        Label formTitle = new Label("Register New Patient");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(15));
        
        // Input fields
        TextField nameField = new TextField(); 
        nameField.setPromptText("Name");
        TextField phoneField = new TextField(); 
        phoneField.setPromptText("Phone");
        TextField emailField = new TextField(); 
        emailField.setPromptText("Email");
        TextField usernameField = new TextField(); 
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField(); 
        passwordField.setPromptText("Password");
        TextField emergencyContactsField = new TextField(); 
        emergencyContactsField.setPromptText("Emergency Contacts (comma-separated)");

        ComboBox<Doctor> physicianComboBox = new ComboBox<>();
        physicianComboBox.setPromptText("Select Physician");
        physicianComboBox.setItems(FXCollections.observableArrayList(Administrator.getDoctors()));
        
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Phone:"), 0, 1);
        form.add(phoneField, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(new Label("Username:"), 0, 3);
        form.add(usernameField, 1, 3);
        form.add(new Label("Password:"), 0, 4);
        form.add(passwordField, 1, 4);
        form.add(new Label("Emergency Contacts:"), 0, 5);
        form.add(emergencyContactsField, 1, 5);
        form.add(new Label("Physician:"), 0, 6);
        form.add(physicianComboBox, 1, 6);

        Button addBtn = new Button("Register Patient");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> {
            // Validate inputs
            if (nameField.getText().isEmpty() || usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert("Name, username and password are required fields.");
                return;
            }
            
            Doctor selectedDoctor = physicianComboBox.getValue();
            if (selectedDoctor == null) {
                showAlert("Please select a physician.");
                return;
            }
            
            ArrayList<String> contacts = new ArrayList<>();
            for (String contact : emergencyContactsField.getText().split(",")) {
                if (!contact.trim().isEmpty()) {
                    contacts.add(contact.trim());
                }
            }

            Patient patient = new Patient(
                    "pat" + (Administrator.getPatients().size() + 1),
                    nameField.getText(), phoneField.getText(), emailField.getText(),
                    usernameField.getText(), passwordField.getText(),
                    contacts, selectedDoctor
            );

            Administrator.registerPatient(patient);
            Administrator.addSystemLog("Registered patient: " + patient.getName());
            table.setItems(FXCollections.observableArrayList(Administrator.getPatients()));
            nameField.clear(); phoneField.clear(); emailField.clear(); usernameField.clear(); passwordField.clear();
            emergencyContactsField.clear(); physicianComboBox.getSelectionModel().clearSelection();
        });

        form.add(addBtn, 1, 7);
        
        VBox formBox = new VBox(10, formTitle, form);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        
        Label tableTitle = new Label("Manage Patients");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        layout.getChildren().addAll(tableTitle, table, formBox);
        return layout;
    }

    private VBox createAdminTab() {
        // create a table to display administrators
        TableView<Administrator> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // set up the table columns
        table.setItems(FXCollections.observableArrayList(Administrator.getAdministrators()));

        // create columns for ID, name, and action buttons
        TableColumn<Administrator, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        TableColumn<Administrator, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        // Action column for update and remove buttons
        TableColumn<Administrator, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");
            private final Button removeBtn = new Button("Remove");
            private final HBox buttonsBox = new HBox(5, updateBtn, removeBtn);
            
            {
                buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
                
                updateBtn.setOnAction(e -> {
                    Administrator admin = getTableView().getItems().get(getIndex());
                    showUpdateAdminDialog(admin, table);
                });
                
                removeBtn.setOnAction(e -> {
                    Administrator admin = getTableView().getItems().get(getIndex());
                    // Confirm before removing
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                                           "Are you sure you want to remove administrator " + admin.getName() + "?",
                                           ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirm Removal");
                    confirm.showAndWait().ifPresent(type -> {
                        if (type == ButtonType.YES) {
                            Administrator.removeAdministrator(admin);
                            Administrator.addSystemLog("Removed administrator: " + admin.getName());
                            table.setItems(FXCollections.observableArrayList(Administrator.getAdministrators()));
                        }
                    });
                });
            }
            
            @Override 
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });

        // add columns to the table
        java.util.Collections.addAll(table.getColumns(), idCol, nameCol, actionsCol);

        // Form for adding new administrators
        Label formTitle = new Label("Register New Administrator");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(15));
        
        TextField nameField = new TextField(); 
        nameField.setPromptText("Name");
        TextField phoneField = new TextField(); 
        phoneField.setPromptText("Phone");
        TextField emailField = new TextField(); 
        emailField.setPromptText("Email");
        TextField usernameField = new TextField(); 
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField(); 
        passwordField.setPromptText("Password");
        
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Phone:"), 0, 1);
        form.add(phoneField, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(new Label("Username:"), 0, 3);
        form.add(usernameField, 1, 3);
        form.add(new Label("Password:"), 0, 4);
        form.add(passwordField, 1, 4);

        Button addBtn = new Button("Register Administrator");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> {
            // Validate fields
            if (nameField.getText().isEmpty() || usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert("Name, username and password are required fields.");
                return;
            }
            
            Administrator admin = new Administrator("adm" + (Administrator.getAdministrators().size() + 1),
                    nameField.getText(), phoneField.getText(), emailField.getText(),
                    usernameField.getText(), passwordField.getText());
            Administrator.registerAdministrator(admin);
            Administrator.addSystemLog("Registered administrator: " + admin.getName());
            table.setItems(FXCollections.observableArrayList(Administrator.getAdministrators()));
            nameField.clear(); phoneField.clear(); emailField.clear(); usernameField.clear(); passwordField.clear();
        });

        form.add(addBtn, 1, 5);
        
        VBox formBox = new VBox(10, formTitle, form);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        
        Label tableTitle = new Label("Manage Administrators");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        layout.getChildren().addAll(tableTitle, table, formBox);
        return layout;
    }

    private VBox createSystemTab() {
        VBox systemPanel = new VBox(15);
        systemPanel.setPadding(new Insets(20));
        
        Label systemTitle = new Label("System Management");
        systemTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // System logs section
        Label logsLabel = new Label("System Logs");
        logsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextArea logsArea = new TextArea();
        logsArea.setEditable(false);
        logsArea.setPrefHeight(200);
        logsArea.setText(String.join("\n", Administrator.getSystemLogs()));
        
        Button refreshLogsButton = new Button("Refresh Logs");
        refreshLogsButton.setOnAction(e -> {
            logsArea.setText(String.join("\n", Administrator.getSystemLogs()));
        });
        
        // Chat management section
        Label chatLabel = new Label("Chat Management");
        chatLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Button clearAllChatsButton = new Button("Clear All Chat Histories");
        clearAllChatsButton.getStyleClass().add("danger-button");
        clearAllChatsButton.setOnAction(event -> {
            // Confirm with the administrator
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Clear All Chat Histories");
            confirmation.setHeaderText("Are you absolutely sure?");
            confirmation.setContentText("This will permanently delete ALL chat histories for ALL users. This action cannot be undone.");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = ChatManager.clearAllChatHistories();
                    if (success) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("All chat histories have been cleared successfully.");
                        alert.showAndWait();
                        
                        // Refresh logs to show the action
                        logsArea.setText(String.join("\n", Administrator.getSystemLogs()));
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Failed to clear chat histories. Please try again later.");
                        alert.showAndWait();
                    }
                }
            });
        });
        
        // Add all components to the system panel
        systemPanel.getChildren().addAll(
            systemTitle,
            new Separator(),
            logsLabel,
            logsArea,
            refreshLogsButton,
            new Separator(),
            chatLabel,
            clearAllChatsButton
        );
        
        return systemPanel;
    }

    /**
     * Shows dialog to update doctor details
     */
    private void showUpdateDoctorDialog(Doctor doctor, TableView<Doctor> table) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Doctor");
        dialog.setHeaderText("Update information for Dr. " + doctor.getName());
        
        // Create fields with current values
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(doctor.getName());
        TextField phoneField = new TextField(doctor.getPhoneNumber());
        TextField emailField = new TextField(doctor.getEmail());
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Update doctor details
                doctor.setName(nameField.getText());
                doctor.setPhoneNumber(phoneField.getText());
                doctor.setEmail(emailField.getText());
                
                // Refresh the table
                table.refresh();
                
                // Log the update
                Administrator.addSystemLog("Updated doctor: " + doctor.getName());
                
                showAlert("Doctor information updated successfully.");
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Shows dialog to update patient details
     */
    private void showUpdatePatientDialog(Patient patient, TableView<Patient> table) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Patient");
        dialog.setHeaderText("Update information for patient " + patient.getName());
        
        // Create fields with current values
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(patient.getName());
        TextField phoneField = new TextField(patient.getPhoneNumber());
        TextField emailField = new TextField(patient.getEmail());
        
        // Emergency contacts
        TextField emergencyContactsField = new TextField(String.join(", ", patient.getEmergencyContacts()));
        
        // Physician selection
        ComboBox<Doctor> physicianComboBox = new ComboBox<>(
            FXCollections.observableArrayList(Administrator.getDoctors())
        );
        physicianComboBox.setValue(patient.getPhysician());
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Emergency Contacts:"), 0, 3);
        grid.add(emergencyContactsField, 1, 3);
        grid.add(new Label("Physician:"), 0, 4);
        grid.add(physicianComboBox, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Update patient details
                patient.setName(nameField.getText());
                patient.setPhoneNumber(phoneField.getText());
                patient.setEmail(emailField.getText());
                
                // Update emergency contacts
                patient.getEmergencyContacts().clear();
                for (String contact : emergencyContactsField.getText().split(",")) {
                    if (!contact.trim().isEmpty()) {
                        patient.addEmergencyContact(contact.trim());
                    }
                }
                
                // Update physician if changed
                Doctor selectedDoctor = physicianComboBox.getValue();
                if (selectedDoctor != null && !selectedDoctor.equals(patient.getPhysician())) {
                    patient.setPhysician(selectedDoctor);
                }
                
                // Refresh the table
                table.refresh();
                
                // Log the update
                Administrator.addSystemLog("Updated patient: " + patient.getName());
                
                showAlert("Patient information updated successfully.");
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Shows dialog to update administrator details
     */
    private void showUpdateAdminDialog(Administrator admin, TableView<Administrator> table) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Administrator");
        dialog.setHeaderText("Update information for " + admin.getName());
        
        // Create fields with current values
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(admin.getName());
        TextField phoneField = new TextField(admin.getPhoneNumber());
        TextField emailField = new TextField(admin.getEmail());
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Update admin details
                admin.setName(nameField.getText());
                admin.setPhoneNumber(phoneField.getText());
                admin.setEmail(emailField.getText());
                
                // Refresh the table
                table.refresh();
                
                // Log the update
                Administrator.addSystemLog("Updated administrator: " + admin.getName());
                
                showAlert("Administrator information updated successfully.");
            }
            return null;
        });
        
        dialog.showAndWait();
    }

    // Helper method to show alerts
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
