package com.ptmd.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private User medico;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Column(name = "ai_diagnosis")
    private String aiDiagnosis;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "mult_class")
    private String multClass;

    @Column(name = "mult_class_confidence")
    private Double multClassConfidence;

    @Column(name = "final_diagnosis")
    private String finalDiagnosis;

    @Column(name = "confirmed")
    private Boolean confirmed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

