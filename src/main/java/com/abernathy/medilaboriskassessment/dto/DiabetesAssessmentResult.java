package com.abernathy.medilaboriskassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiabetesAssessmentResult {
    private Integer patientId;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private String riskLevel;
}
