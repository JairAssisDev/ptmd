package com.ptmd.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmDiagnosisRequest {
    
    @NotNull(message = "Diagnóstico final é obrigatório")
    private Diagnosis finalDiagnosis;
}

