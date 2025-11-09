package com.abernathy.medilaboriskassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//@Data
//@AllArgsConstructor
//public class DiabetesAssessmentResult {
//    private Integer patientId;
//    private String firstName;
//    private String lastName;
//    private Integer age;
//    private String gender;
//    private String riskLevel;
//}


public class DiabetesAssessmentResult {
    private String riskLevel;

    public DiabetesAssessmentResult(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    // Getter
    public String getRiskLevel() { return riskLevel; }
}

