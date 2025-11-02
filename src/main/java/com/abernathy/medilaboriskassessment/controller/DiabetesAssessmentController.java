package com.abernathy.medilaboriskassessment.controller;

import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.Patient;
import com.abernathy.medilaboriskassessment.dto.MedicalNote;
import com.abernathy.medilaboriskassessment.service.DiabetesAssessmentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@RestController
@RequestMapping("/assess")

@Slf4j
public class DiabetesAssessmentController {

    private final DiabetesAssessmentService assessmentService;
    private final RestTemplate restTemplate;
    private final String gatewayBaseUrl;
    //private final DiabetesAssessmentResult diabetesAssessmentResult;

    public DiabetesAssessmentController(RestTemplate restTemplate,
                                        @Value("${gateway.base-url}") String gatewayBaseUrl,
                                       // DiabetesAssessmentResult diabetesAssessmentResult,
                                        DiabetesAssessmentService diabetesAssessmentService)
    {
        this.restTemplate = restTemplate;
        this.gatewayBaseUrl = gatewayBaseUrl;
        log.info("Assessment repo started with gatewayBaseUrl={}", gatewayBaseUrl);
        //this.diabetesAssessmentResult = diabetesAssessmentResult;
        this.assessmentService = diabetesAssessmentService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<DiabetesAssessmentResult> assessRisk(@PathVariable int patientId, HttpServletRequest request) {
        HttpEntity<Void> entity = createEntityWithSession(request);
        log.info("Calculating risk assessment for patient id:", patientId);
        // Fetch patient
        Patient patient = restTemplate.exchange(
                gatewayBaseUrl + "/api/proxy/patients?id=" + patientId,
                HttpMethod.GET,
                entity,
                Patient.class
        ).getBody();

        //Fetch Medical Notes
        MedicalNote[] notesArray = restTemplate.exchange(
                gatewayBaseUrl + "/api/proxy/notes/history?patientId=" + patientId,
                HttpMethod.GET,
                entity,
                MedicalNote[].class
        ).getBody();

        DiabetesAssessmentResult result = assessmentService.assessRisk(patient, notesArray);
        return ResponseEntity.ok(result);
    }

    // -----------------------
    // Helper methods
    // -----------------------
    private HttpEntity<Void> createEntityWithSession(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        String jsessionId = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                .filter(c -> "JSESSIONID".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (jsessionId != null) {
            headers.add(HttpHeaders.COOKIE, "JSESSIONID=" + jsessionId);
        }
        return new HttpEntity<>(headers);
    }
}

