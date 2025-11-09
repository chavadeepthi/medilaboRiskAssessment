package com.abernathy.medilaboriskassessment.service;



import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.Patient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskAssessmentService {

    private static final List<String> TRIGGER_TERMS = List.of(
            "Hemoglobin A1C", "Microalbumin", "Height", "Weight",
            "Smoking", "Abnormal", "Cholesterol", "Dizziness",
            "Relapse", "Reaction", "Antibody"
    );

    public DiabetesAssessmentResult assessRisk(Patient patient, List<String> notes) {
        int triggerCount = (int) notes.stream()
                .flatMap(note -> TRIGGER_TERMS.stream()
                        .filter(term -> note.toLowerCase().contains(term.toLowerCase())))
                .count();

        String riskLevel = calculateRiskLevel(patient, triggerCount);
        return new DiabetesAssessmentResult(riskLevel);
    }

    private String calculateRiskLevel(Patient patient, int triggerCount) {
        int age = patient.getAge();
        String gender = patient.getGender();

        // Early Onset
        if ((age > 30 && triggerCount >= 8) ||
                (age < 30 && "M".equalsIgnoreCase(gender) && triggerCount >= 5) ||
                (age < 30 && "F".equalsIgnoreCase(gender) && triggerCount >= 6)) {
            return "Early Onset";
        }

        // In Danger
        if ((age > 30 && triggerCount >= 6) ||
                (age < 30 && "M".equalsIgnoreCase(gender) && (triggerCount == 3 || triggerCount == 4)) ||
                (age < 30 && "F".equalsIgnoreCase(gender) && (triggerCount == 4 || triggerCount == 5))) {
            return "In Danger";
        }

        // Borderline
        if (age > 30 && triggerCount >= 2 && triggerCount <= 5) {
            return "Borderline";
        }

        return "None";
    }
}
