package com.ptmd.repository;

import com.ptmd.entity.Consultation;
import com.ptmd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByMedico(User medico);
    @Query("SELECT DISTINCT c FROM Consultation c LEFT JOIN FETCH c.images WHERE c.medico = :medico ORDER BY c.createdAt DESC")
    List<Consultation> findByMedicoOrderByCreatedAtDesc(@Param("medico") User medico);
    
    @Query("SELECT DISTINCT c FROM Consultation c LEFT JOIN FETCH c.images WHERE c.medico = :medico " +
           "AND (:nome IS NULL OR LOWER(c.patient.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:cpf IS NULL OR c.patient.cpf = :cpf) " +
           "ORDER BY c.createdAt DESC")
    List<Consultation> findByMedicoWithFilters(
        @Param("medico") User medico,
        @Param("nome") String nome,
        @Param("cpf") String cpf
    );

    @Query("SELECT DISTINCT c FROM Consultation c LEFT JOIN FETCH c.images WHERE c.id = :id")
    Consultation findByIdWithImages(@Param("id") Long id);
}

