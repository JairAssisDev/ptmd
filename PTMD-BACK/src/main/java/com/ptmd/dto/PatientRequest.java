package com.ptmd.dto;

import com.ptmd.entity.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;
    
    @NotNull(message = "Sexo é obrigatório")
    private Patient.Sexo sexo;
    
    private LocalDate dataNascimento;
}

