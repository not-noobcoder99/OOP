
Remote Patient Monitoring System (RPMS)
Overview
The Remote Patient Monitoring System (RPMS) is a Java-based application designed to facilitate remote healthcare services.
It provides a platform for doctors, patients, and administrators to interact seamlessly.
The system includes features such as patient monitoring, appointment scheduling, video consultations, chat functionality, and health data management.

This project is built using JavaFX for the user interface and Maven for dependency management.

Features<br>

Doctor Dashboard: Manage patients, view appointments, provide feedback, and engage in video consultations.
<br>Patient Dashboard: View health data, interact with doctors, and access prescriptions.
<br>Administrator Dashboard: Manage system users, logs, and overall system settings.
<br>Chat Functionality: Real-time communication between doctors and patients.
<br>Video Consultations: Secure video calls for remote consultations.
<br>Health Data Management: Track and manage vital signs and medical history.
<br>Report Generation: Generate detailed patient health reports.

Prerequisites
Before running the project, ensure you have the following installed:

Java Development Kit (JDK): Version 17 or higher.<br>
Apache Maven: For building and managing dependencies.<br>
JavaFX SDK: Ensure JavaFX is configured in your environment.<br>

Project Structure<br>

Installation and Setup<br>
Step 1: Clone the Repository<br>
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
  Administrator: Manage users and system settings.<br>Doctor: Access patient data, provide feedback, and manage appointments.<br>Patient: View health data, interact with doctors, and access prescriptions.
3. Dashboards <br>
Navigate through the intuitive dashboards to access various features.
4. Chat and Video Calls <br>
Use the chat interface for real-time communication.<br>
Join video consultations directly from the dashboard.
5. Generate Reports<br>
Generate detailed health reports for patients from the doctor or administrator dashboard.<br>
Main Application<br>
Main.java: Entry point of the application.<br>
Utilities<br>
DataManager.java: Handles data persistence.<br>
ChatManager.java: Manages chat functionality.

Reports<br>
ReportGenerator.java: Generates patient health reports.<br>
Sample Data<br>
The application initializes with sample data for testing purposes:<br>

Doctor: Username: rohaan_doc1, Password: rohaan123

Patient: Username: hamza, Password: hamza123

Administrator: Username: ut_adm, Password: adm123

Troubleshooting
Common Issues
JavaFX Runtime Error: Ensure JavaFX is properly configured in your environment.<br>
Data Directory Issues: Ensure the data/ directory is writable.<br>
Debugging<br>
Use the provided debug script:<br>
 debug-run.bat
License<br>
This project is licensed under the MIT License. See the LICENSE file for details.

For any questions or support, please contact:
Email: utanveer.bsai24seecs@seecs.edu.pk
