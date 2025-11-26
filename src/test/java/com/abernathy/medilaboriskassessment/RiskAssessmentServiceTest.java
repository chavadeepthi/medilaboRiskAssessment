package com.abernathy.medilaboriskassessment;

import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.MedicalNote;
import com.abernathy.medilaboriskassessment.dto.Patient;
import com.abernathy.medilaboriskassessment.service.RiskAssessmentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RiskAssessmentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RiskAssessmentService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject a dummy gateway URL
        service.gateWayURL = "http://localhost:8080";
    }

    @Test
    void testAssessRiskCheck_withNotesAndPatient() {
        int patientId = 1;
        Patient patient = new Patient();
        patient.setPatientId(patientId);
        patient.setAge(40);
        patient.setGender("M");

        MedicalNote[] notesArray = new MedicalNote[] {
                new MedicalNote("1", 1, "Patient has Hemoglobin A1C issue", "2025-01-01"),
                new MedicalNote("2", 1, "Cholesterol levels high", "2025-01-02")
        };


        Cookie jsession = new Cookie("JSESSIONID", "abc123");
        when(request.getCookies()).thenReturn(new Cookie[]{jsession});

        // Mock RestTemplate responses
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/proxy/patients?id=1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Patient.class)
        )).thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));

        when(restTemplate.exchange(
                eq("http://localhost:8080/api/proxy/notes/history?patientId=1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(MedicalNote[].class)
        )).thenReturn(new ResponseEntity<>(notesArray, HttpStatus.OK));

        DiabetesAssessmentResult result = service.assessRiskCheck((long) patientId, request);

        assertNotNull(result);
        assertEquals("Borderline", result.getRiskLevel());
    }

    @Test
    void testAssessRisk_calculateRiskLevels() {
        Patient patient = new Patient();
        patient.setAge(35);
        patient.setGender("M");

        // Borderline case
        DiabetesAssessmentResult result1 = service.assessRisk(patient, List.of("Hemoglobin A1C"));
        //assertEquals("Borderline", result1.getRiskLevel());
        assertEquals("None", result1.getRiskLevel());

        // In Danger
        DiabetesAssessmentResult result2 = service.assessRisk(patient, List.of(
                "Hemoglobin A1C", "Microalbumin", "Weight", "Height", "Smoking", "Abnormal"
        ));
        assertEquals("In Danger", result2.getRiskLevel());

        // Early Onset
        DiabetesAssessmentResult result3 = service.assessRisk(patient, List.of(
                "Hemoglobin A1C", "Microalbumin", "Weight", "Height", "Smoking", "Abnormal",
                "Cholesterol", "Dizziness"
        ));
        assertEquals("Early Onset", result3.getRiskLevel());

        // None
        DiabetesAssessmentResult result4 = service.assessRisk(patient, List.of());
        assertEquals("None", result4.getRiskLevel());
    }

    @Test
    void testAssessRisk_withNullAgeAndDob() {
        Patient patient = new Patient();
        patient.setDob(LocalDate.now().minusYears(25));
        patient.setGender("F");

        DiabetesAssessmentResult result = service.assessRisk(patient, List.of(
                "Hemoglobin A1C", "Microalbumin", "Height", "Weight"
        ));
        assertNotNull(result);
        assertEquals("In Danger", result.getRiskLevel());
        assertEquals(25, patient.getAge()); // Age should be computed from DOB
    }

    @Test
    void testCreateEntityWithSession_noCookies() {
        when(request.getCookies()).thenReturn(null);

        // Using reflection to test private method
        HttpEntity<Void> entity = service.createEntityWithSession(request);
        assertNotNull(entity);
        assertTrue(entity.getHeaders().isEmpty());
    }

    @Test
    void testCreateEntityWithSession_withCookie() {
        Cookie jsession = new Cookie("JSESSIONID", "abc123");
        when(request.getCookies()).thenReturn(new Cookie[]{jsession});

        HttpEntity<Void> entity = service.createEntityWithSession(request);
        assertNotNull(entity);
        assertEquals("JSESSIONID=abc123", entity.getHeaders().getFirst(HttpHeaders.COOKIE));
    }
}

