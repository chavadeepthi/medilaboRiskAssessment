package com.abernathy.medilaboriskassessment;

import com.abernathy.medilaboriskassessment.controller.DiabetesAssessmentController;
import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.Patient;
import com.abernathy.medilaboriskassessment.dto.RiskAssessmentRequest;
import com.abernathy.medilaboriskassessment.service.RiskAssessmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiabetesAssessmentController.class)
@AutoConfigureMockMvc(addFilters = true)   // Enable Spring Security
class DiabetesAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskAssessmentService riskService;

    @Autowired
    private ObjectMapper objectMapper;

    // ----------------------------------------------------------------------
    //     TEST: POST /risk/assess
    // ----------------------------------------------------------------------
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAssessRisk() throws Exception {

        // Prepare request body
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setAge(45);
        patient.setGender("M");

        RiskAssessmentRequest req = new RiskAssessmentRequest();
        req.setPatient(patient);
        req.setNotes(List.of("Hemoglobin A1C", "Cholesterol"));

        // Service return mock
        DiabetesAssessmentResult mockResult = new DiabetesAssessmentResult();
        mockResult.setRiskLevel("Borderline");

        Mockito.when(riskService.assessRisk(any(Patient.class), anyList()))
                .thenReturn(mockResult);

        mockMvc.perform(post("/risk/assess")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------------------
    //     TEST: POST /risk/check?patientId=1
    // ----------------------------------------------------------------------
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAssessRiskCheck() throws Exception {

        DiabetesAssessmentResult mockResult = new DiabetesAssessmentResult();
        mockResult.setRiskLevel("In Danger");

        Mockito.when(riskService.assessRiskCheck(eq(1L), any()))
                .thenReturn(mockResult);

        mockMvc.perform(post("/risk/check")
                        .with(csrf())
                        .param("patientId", "1"))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------------------
    //     TEST: SECURITY — NO USER → 401 or 403
    // ----------------------------------------------------------------------
//    @Test
//    void testUnauthorizedAccess() throws Exception {
//
//        mockMvc.perform(post("/risk/check")
//                        .param("patientId", "1"))
//                .andExpect(status().isUnauthorized());
//        // or .isForbidden() depending on your security config
//    }
}

