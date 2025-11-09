package com.abernathy.medilaboriskassessment.controller;

import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.RiskAssessmentRequest;
import com.abernathy.medilaboriskassessment.service.RiskAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/risk")
@Slf4j
public class DiabetesAssessmentController {
    private final RiskAssessmentService riskService;

    public DiabetesAssessmentController(RiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    @PostMapping("/assess")
    public ResponseEntity<DiabetesAssessmentResult> assessRisk(@RequestBody RiskAssessmentRequest request) {
        log.info("Performing Risk Analysis for Patient :{}",request.getPatient().getLastName());
        DiabetesAssessmentResult result = riskService.assessRisk(request.getPatient(), request.getNotes());
        return ResponseEntity.ok(result);
    }
}

