package com.ptmd.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ConsultationRequest {
    
    @NotNull(message = "Dados do paciente são obrigatórios")
    private PatientRequest patient;
    
    @NotNull(message = "Imagem é obrigatória")
    private MultipartFile image;
}

