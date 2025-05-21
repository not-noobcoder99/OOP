package com.rpms.Reports;

import com.rpms.DoctorPatientInteraction.Feedback;
import com.rpms.DoctorPatientInteraction.Prescription;
import com.rpms.HealthData.VitalSign;
import com.rpms.Users.Patient;
import com.rpms.Users.Doctor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

/**
 * Generates comprehensive reports of patient data.
 * This class provides static methods to create patient health reports in plain text format.
 */
public class ReportGenerator {

    /**
     * Generates a detailed report of a patient's health data and saves it to a file.
     * The report includes:
     * - Patient information
     * - Doctor information
     * - Vital signs history
     * - Medical feedback
     * - Prescriptions
     * 
     * @param patient The patient whose report should be generated
     * @param file The file where the report will be saved
     */
    public static void generatePatientReport(Patient patient, File file) {
        try (FileWriter writer = new FileWriter(file)) {

            // Header
            writer.write("Patient Report - " + patient.getName() + " (" + patient.getId() + ")\n");
            writer.write("--------------------------------------------------\n");

            // Doctor info
            Doctor doctor = patient.getPhysician();
            if (doctor != null) {
                writer.write("Assigned Doctor: " + doctor.getName() + "\n");
                writer.write("Contact: " + doctor.getEmail() + "\n\n");
            }

            // Vitals
            writer.write("Vitals History:\n");
            ArrayList<VitalSign> vitals = patient.getVitals().getVitals();
            for (VitalSign v : vitals) {
                writer.write(v.toString() + "\n");
            }

            writer.write("\nFeedback:\n");
            ArrayList<Feedback> feedbacks = patient.getFeedbacks();
            for (Feedback f : feedbacks) {
                writer.write(f.toString());
                ArrayList<Prescription> prescriptions = f.getPrescriptions();
                if (prescriptions != null && !prescriptions.isEmpty()) {
                    writer.write("   Prescriptions:\n");
                    for (Prescription p : prescriptions) {
                        writer.write("     • " + p.toString() + "\n");
                    }
                }
            }

            System.out.println("Report generated: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to generate report: " + e.getMessage());
        }
    }
}
