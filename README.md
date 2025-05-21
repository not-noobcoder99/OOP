# 🏥 Remote Patient Monitoring System (RPMS)

<div align="center">
  
  
  
  [![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
  [![JavaFX](https://img.shields.io/badge/JavaFX-17%2B-blue.svg)](https://openjfx.io/)
  [![Maven](https://img.shields.io/badge/Maven-3.8.1-red.svg)](https://maven.apache.org/)
  [![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
  
</div>

## 📋 Overview

The **Remote Patient Monitoring System (RPMS)** is a comprehensive Java-based application designed to revolutionize healthcare delivery through remote services. This platform seamlessly connects doctors, patients, and administrators in a unified digital environment.

Built with JavaFX for an intuitive user interface and Maven for robust dependency management, RPMS offers a complete solution for modern telehealth needs.

## ✨ Key Features

<div align="center">
  <table>
    <tr>
      <td align="center">👨‍⚕️</td>
      <td><b>Doctor Dashboard</b></td>
      <td>Manage patients, view appointments, provide feedback, and conduct video consultations</td>
    </tr>
    <tr>
      <td align="center">🧑‍⚕️</td>
      <td><b>Patient Dashboard</b></td>
      <td>View health data, interact with doctors, and access prescriptions</td>
    </tr>
    <tr>
      <td align="center">👨‍💼</td>
      <td><b>Administrator Dashboard</b></td>
      <td>Manage system users, logs, and overall system settings</td>
    </tr>
    <tr>
      <td align="center">💬</td>
      <td><b>Chat Functionality</b></td>
      <td>Real-time communication between doctors and patients</td>
    </tr>
    <tr>
      <td align="center">📹</td>
      <td><b>Video Consultations</b></td>
      <td>Secure video calls for remote consultations</td>
    </tr>
    <tr>
      <td align="center">📊</td>
      <td><b>Health Data Management</b></td>
      <td>Track and manage vital signs and medical history</td>
    </tr>
    <tr>
      <td align="center">📝</td>
      <td><b>Report Generation</b></td>
      <td>Generate detailed patient health reports</td>
    </tr>
  </table>
</div>

## 🛠️ Prerequisites

Before running the project, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 17 or higher
- **Apache Maven**: For building and managing dependencies
- **JavaFX SDK**: Configured in your development environment

## 📂 Project Structure

```
RPMS APP/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com.rpms/
│   │   │   │   ├── Main.java                # Main entry point of the application
│   │   │   │   ├── GUI/                    # JavaFX GUI components
│   │   │   │   ├── controllers/            # Controllers for handling UI logic
│   │   │   │   ├── utilities/              # Utility classes (e.g., data management, serialization)
│   │   │   │   ├── Reports/                # Report generation logic
│   │   │   │   ├── DoctorPatientInteraction/ # Models for doctor-patient interactions
│   │   │   │   ├── NotificationsAndReminders/ # Notification services
│   │   │   │   ├── AppointmentHandling/    # Appointment management
│   │   │   │   ├── EmergencyAlertSystem/   # Emergency alert features
│   │   │   │   ├── Users/                  # User models (Doctor, Patient, Administrator)
│   │   ├── resources/
│   │   │   ├── fxml/                       # FXML files for UI layouts
│   │   │   ├── css/                        # Stylesheets for UI
│   │   │   ├── images/                     # Images and icons
│   ├── test/                               # Unit tests
├── pom.xml                                 # Maven configuration file
├── README.md                               # Project documentation
├── setup-and-run.bat                       # Batch script for setup and running
├── debug-run.bat                           # Batch script for debugging
```

## 🚀 Installation and Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/Remote-Patient-Monitoring-System-RPMS.git
cd Remote-Patient-Monitoring-System-RPMS
```

### Step 2: Configure JavaFX

Ensure JavaFX is properly configured in your environment. Add the JavaFX SDK to your PATH or configure it in your IDE.

### Step 3: Build the Project

Use Maven to build the project:

```bash
mvn clean install
```

### Step 4: Run the Application

Run the application using Maven:

```bash
mvn javafx:run
```

Alternatively, use the provided batch script:

```bash
run.bat
```

## 💻 Usage

### 1. Login

<div align="center">
  Login Screen
</div>

Choose your role to access the system:
- **Administrator**: Manage users and system settings
- **Doctor**: Access patient data, provide feedback, and manage appointments
- **Patient**: View health data, interact with doctors, and access prescriptions

### 2. Dashboards

Navigate through the intuitive dashboards to access various features.

<div align="center">
        Dashboard
</div>

### 3. Chat and Video Calls

- Use the chat interface for real-time communication
- Join video consultations directly from the dashboard

### 4. Generate Reports

Generate detailed health reports for patients from the doctor or administrator dashboard.

## 📚 Core Components

### Main Application
- **Main.java**: Entry point of the application

### Utilities
- **DataManager.java**: Handles data persistence
- **ChatManager.java**: Manages chat functionality

### Reports
- **ReportGenerator.java**: Generates patient health reports

## 🔑 Sample Login Credentials

The application initializes with sample data for testing purposes:

| Role | Username | Password |
|------|----------|----------|
| Doctor | rohaan_doc1 | rohaan123 |
| Patient | hamza | hamza123 |
| Administrator | ut_adm | adm123 |

## ⚠️ Troubleshooting

### Common Issues

- **JavaFX Runtime Error**: Ensure JavaFX is properly configured in your environment
- **Data Directory Issues**: Ensure the data/ directory is writable

### Debugging

Use the provided debug script:

```bash
debug-run.bat
```

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## 📞 Contact

For any questions or support, please contact:

- **Email**: utanveer.bsai24seecs@seecs.edu.pk


---

