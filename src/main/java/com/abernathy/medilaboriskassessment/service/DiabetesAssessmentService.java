package com.abernathy.medilaboriskassessment.service;

import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.MedicalNote;
import com.abernathy.medilaboriskassessment.dto.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiabetesAssessmentService {

    public DiabetesAssessmentResult assessRisk(Patient patient, MedicalNote[] notes) {

        int triggerCount = countTriggerTerms(notes);
        String riskLevel = calculateRiskLevel(patient, triggerCount);

        return new DiabetesAssessmentResult(patient.getPatientId(), patient.getFirstName(), patient.getLastName(), patient.getAge(), patient.getGender(), riskLevel);
    }

    private int countTriggerTerms(MedicalNote[] notes) {
        List<String> triggers = List.of("Hemoglobin A1C", "Microalbumin", "Height", "Weight", "Smoking",
                "Abnormal", "Cholesterol", "Dizziness", "Relapse", "Reaction", "Antibody");
        String allNotes = Arrays.stream(notes)
                .map(MedicalNote::getNote)
                .collect(Collectors.joining(" "))
                .toLowerCase();
        return (int) triggers.stream()
                .filter(t -> allNotes.contains(t.toLowerCase()))
                .count();
    }

    private String calculateRiskLevel(Patient p, int triggers) {
        int age = p.getAge();
        String gender = p.getGender().toLowerCase();
        return "Deepthi Chava";
    }
}

