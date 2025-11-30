package com.ptmd.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmDiagnosisRequest {
    
    @NotBlank(message = "Diagnóstico final é obrigatório")
    private String finalDiagnosis;
}

