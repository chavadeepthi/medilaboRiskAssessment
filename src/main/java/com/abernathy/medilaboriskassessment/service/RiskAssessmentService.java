package com.abernathy.medilaboriskassessment.service;
import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.MedicalNote;
import com.abernathy.medilaboriskassessment.dto.Patient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Risk Assessment calculation service */
@Service
@Slf4j
public class RiskAssessmentService {
    private final RestTemplate restTemplate;

    public RiskAssessmentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${gateway.base-url}")
    public String gateWayURL;

    public DiabetesAssessmentResult assessRiskCheck(Long patientId, HttpServletRequest request) {

        HttpEntity<Void> entity = createEntityWithSession(request);

        // Fetch patient
        Patient patient = restTemplate.exchange(
                gateWayURL + "/api/proxy/patients?id=" + patientId,
                HttpMethod.GET,
                entity,
                Patient.class
        ).getBody();

        log.info("Fetched patient: {}", patient);

        // Fetch notes
        MedicalNote[] notesArray = restTemplate.exchange(
                gateWayURL + "/api/proxy/notes/history?patientId=" + patientId,
                HttpMethod.GET,
                entity,
                MedicalNote[].class
        ).getBody();

        log.info("Fetched Notes: {}", notesArray);

        List<String> notesText = notesArray != null ?
                Arrays.stream(notesArray)
                        .map(MedicalNote::getNote)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return assessRisk(patient, notesText);
    }


    private static final List<String> TRIGGER_TERMS = List.of(
            "Hemoglobin A1C", "Microalbumin", "Height", "Weight",
            "Smoking", "Abnormal", "Cholesterol", "Dizziness",
            "Relapse", "Reaction", "Antibody"
    );

    public DiabetesAssessmentResult assessRisk(Patient patient, List<String> notes) {

        if (patient.getAge() == null && patient.getDob() != null) {
            int computedAge = Period.between(patient.getDob(), LocalDate.now()).getYears();
            patient.setAge(computedAge);
        }

        int triggerCount = (int) notes.stream()
                .flatMap(note -> TRIGGER_TERMS.stream()
                        .filter(term -> note.toLowerCase().contains(term.toLowerCase())))
                .count();

        String riskLevel = calculateRiskLevel(patient, triggerCount);
        DiabetesAssessmentResult result = new DiabetesAssessmentResult();
        result.setRiskLevel(riskLevel);
        return result;
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

    // -----------------------
    // Helper methods
    // -----------------------
    public HttpEntity<Void> createEntityWithSession(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(c -> "JSESSIONID".equals(c.getName()))
                    .findFirst()
                    .ifPresent(cookie -> headers.add(HttpHeaders.COOKIE, "JSESSIONID=" + cookie.getValue()));
        }
        return new HttpEntity<>(headers);
    }
}
