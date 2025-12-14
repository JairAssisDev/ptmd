package com.ptmd.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ConsultationRequest {
    
    @NotNull(message = "Dados do paciente são obrigatórios")
    private PatientRequest patient;
    
    @NotEmpty(message = "Pelo menos uma imagem é obrigatória")
    private List<MultipartFile> images;
}

