package com.ptmd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponse {
    private Long id;
    private PatientResponse patient;
    private String aiDiagnosis;
    private Double confidence;
    private String multClass;
    private Double multClassConfidence;
    private String finalDiagnosis;
    private Boolean confirmed;
    private LocalDateTime createdAt;
}

