package com.abernathy.medilaboriskassessment.controller;

import com.abernathy.medilaboriskassessment.dto.DiabetesAssessmentResult;
import com.abernathy.medilaboriskassessment.dto.RiskAssessmentRequest;
import com.abernathy.medilaboriskassessment.service.RiskAssessmentService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
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
        DiabetesAssessmentResult result = riskService.assessRisk(request.getPatient(), request.getNotes());
        return ResponseEntity.ok(result);
    }
}
//    private HttpEntity<Void> createEntityWithSession(HttpServletRequest request) {
//        HttpHeaders headers = new HttpHeaders();
//        if (request.getCookies() != null) {
//            Arrays.stream(request.getCookies())
//                    .filter(c -> "JSESSIONID".equals(c.getName()))
//                    .findFirst()
//                    .ifPresent(cookie -> headers.add(HttpHeaders.COOKIE, "JSESSIONID=" + cookie.getValue()));
//        }
//        return new HttpEntity<>(headers);
//    }
//}
