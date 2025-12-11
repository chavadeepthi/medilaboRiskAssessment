package com.abernathy.medilaboriskassessment.dto;


import java.util.List;

/** Risk Assessment get Patient and get Medical note */

public class RiskAssessmentRequest {
    private Patient patient;
    private List<String> notes;

    // Getters and Setters
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }
}

