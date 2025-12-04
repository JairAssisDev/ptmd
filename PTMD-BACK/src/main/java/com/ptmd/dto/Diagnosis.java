package com.ptmd.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Diagnosis {
    NORMAL("Normal"),
    AOM("aom"),
    CSOM("csom"),
    EARWAX("earwax"),
    EXTERNAL_EAR_INFECTIONS("ExternalEarInfections"),
    TYMPANOSKLEROS("tympanoskleros");

    private final String value;

    Diagnosis(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Diagnosis fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // Tenta encontrar pelo valor (case-insensitive)
        for (Diagnosis diagnosis : Diagnosis.values()) {
            if (diagnosis.value.equalsIgnoreCase(value)) {
                return diagnosis;
            }
        }
        
        // Tenta encontrar pelo nome do enum (case-insensitive)
        try {
            return Diagnosis.valueOf(value.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            // Ignora e continua
        }
        
        throw new IllegalArgumentException("Diagnóstico inválido: '" + value + "'. Valores permitidos: Normal, aom, csom, earwax, ExternalEarInfections, tympanoskleros");
    }
}

