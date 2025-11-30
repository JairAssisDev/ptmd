package com.ptmd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AiPredictionResponse {
    private List<Prediction> predictions;
    private String error;

    @Data
    public static class Prediction {
        // Tratamento para inconsistência: "class" (minúsculo) ou "Class" (maiúsculo)
        @JsonProperty("class")
        private String classLower;
        
        @JsonProperty("Class")
        private String classUpper;
        
        @JsonProperty("Probabilidade")
        private Double probabilidade;
        
        @JsonProperty("MultClass")
        private String multClass;
        
        @JsonProperty("ProbabilidadeMultClass")
        private Double probabilidadeMultClass;

        // Método helper para obter a classe (trata ambos os casos)
        public String getClassValue() {
            return classUpper != null ? classUpper : classLower;
        }
    }
}

