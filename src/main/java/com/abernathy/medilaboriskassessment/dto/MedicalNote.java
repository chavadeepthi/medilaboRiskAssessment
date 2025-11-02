package com.abernathy.medilaboriskassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalNote {

    private Integer id;
    private Integer patientId;
    private String note;       // The text where trigger terms are located
    private String date;       // Optional: date of note

}

