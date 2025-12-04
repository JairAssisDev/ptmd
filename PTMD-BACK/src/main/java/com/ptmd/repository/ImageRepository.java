package com.ptmd.repository;

import com.ptmd.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    @Query("SELECT i FROM Image i JOIN FETCH i.consultation c JOIN FETCH c.patient WHERE c.confirmed = true AND c.aiDiagnosis IS NOT NULL AND c.finalDiagnosis IS NOT NULL")
    List<Image> findConfirmedImagesWithDiagnosis();
}

