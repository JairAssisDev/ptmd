package com.ptmd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private Long totalImages;
    private Long totalConsultations;
    private Long totalPatients;
}

