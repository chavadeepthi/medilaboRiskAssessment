package com.abernathy.medilaboriskassessment.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Patient DTO */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Integer patientId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private Integer age;       // Important for risk calculation
    private String gender;     // "M" or "F"

}

