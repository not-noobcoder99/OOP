
Remote Patient Monitoring System (RPMS)
Overview
The Remote Patient Monitoring System (RPMS) is a Java-based application designed to facilitate remote healthcare services.
It provides a platform for doctors, patients, and administrators to interact seamlessly.
The system includes features such as patient monitoring, appointment scheduling, video consultations, chat functionality, and health data management.

This project is built using JavaFX for the user interface and Maven for dependency management.

Features

Doctor Dashboard: Manage patients, view appointments, provide feedback, and engage in video consultations.
Patient Dashboard: View health data, interact with doctors, and access prescriptions.
Administrator Dashboard: Manage system users, logs, and overall system settings.
Chat Functionality: Real-time communication between doctors and patients.
Video Consultations: Secure video calls for remote consultations.
Health Data Management: Track and manage vital signs and medical history.
Report Generation: Generate detailed patient health reports.

Prerequisites
Before running the project, ensure you have the following installed:

Java Development Kit (JDK): Version 17 or higher.
Apache Maven: For building and managing dependencies.
JavaFX SDK: Ensure JavaFX is configured in your environment.

Project Structure
Installation and Setup
Step 1: Clone the Repository
Clone the project to your local machine:
![image](https://github.com/user-attachments/assets/39bb011e-279f-4f3f-a457-307c6a74783f)

Step 2: Configure JavaFX
Ensure JavaFX is properly configured in your environment. Add the JavaFX SDK to your PATH or configure it in your IDE.

Step 3: Build the Project
Use Maven to build the project:

![image](https://github.com/user-attachments/assets/65af421e-e4ce-4cee-b80a-b3b8f1ac0a75)


Step 4: Run the Application
Run the application using Maven:
![image](https://github.com/user-attachments/assets/799bd611-16d9-4ab9-a16c-e37b73c86845)


Alternatively, use the provided batch script:

![image](https://github.com/user-attachments/assets/643b1993-0b88-42d4-9646-d69206509b72)


Usage
1. Login
Administrator: Manage users and system settings.
Doctor: Access patient data, provide feedback, and manage appointments.
Patient: View health data, interact with doctors, and access prescriptions.
2. Dashboards
Navigate through the intuitive dashboards to access various features.
3. Chat and Video Calls
Use the chat interface for real-time communication.
Join video consultations directly from the dashboard.
4. Generate Reports
Generate detailed health reports for patients from the doctor or administrator dashboard.
Key Classes and Files
Main Application
Main.java: Entry point of the application.
GUI Components
LoginScreen.java: Handles user login.
DoctorDashboard.java: Doctor's dashboard interface.
PatientDashboard.java: Patient's dashboard interface.
Utilities
DataManager.java: Handles data persistence.
ChatManager.java: Manages chat functionality.

Reports
ReportGenerator.java: Generates patient health reports.
Sample Data
The application initializes with sample data for testing purposes:

Doctor: Username: rohaan_doc1, Password: rohaan123
Patient: Username: hamza, Password: hamza123
Administrator: Username: ut_adm, Password: adm123

Troubleshooting
Common Issues
JavaFX Runtime Error: Ensure JavaFX is properly configured in your environment.
Data Directory Issues: Ensure the data/ directory is writable.
Debugging
Use the provided debug script:
 debug-run.bat
License
This project is licensed under the MIT License. See the LICENSE file for details.

For any questions or support, please contact:
Email: utanveer.bsai24seecs@seecs.edu.pk
