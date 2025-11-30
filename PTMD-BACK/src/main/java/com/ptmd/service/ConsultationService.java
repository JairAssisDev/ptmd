package com.ptmd.service;

import com.ptmd.dto.*;
import com.ptmd.entity.*;
import com.ptmd.repository.ConsultationRepository;
import com.ptmd.repository.ImageRepository;
import com.ptmd.repository.PatientRepository;
import com.ptmd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AiService aiService;

    @Transactional
    public ConsultationResponse createConsultation(ConsultationRequest request) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User medico = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        // Criar ou buscar paciente
        Patient patient = new Patient();
        patient.setNome(request.getPatient().getNome());
        patient.setSexo(request.getPatient().getSexo());
        patient.setDataNascimento(request.getPatient().getDataNascimento());
        patient = patientRepository.save(patient);

        // Salvar imagem
        String filePath = fileStorageService.storeFile(request.getImage());

        // Criar consulta
        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setMedico(medico);

        // Integração com IA
        AiPredictionResponse aiResponse = aiService.predict(request.getImage());
        
        if (aiResponse.getPredictions() != null && !aiResponse.getPredictions().isEmpty()) {
            AiPredictionResponse.Prediction prediction = aiResponse.getPredictions().get(0);
            consultation.setAiDiagnosis(prediction.getClassValue());
            consultation.setConfidence(prediction.getProbabilidade());
            
            if (prediction.getMultClass() != null && !prediction.getMultClass().isEmpty()) {
                consultation.setMultClass(prediction.getMultClass());
                consultation.setMultClassConfidence(prediction.getProbabilidadeMultClass());
            }
        }

        consultation = consultationRepository.save(consultation);

        // Criar registro de imagem
        Image image = new Image();
        image.setConsultation(consultation);
        image.setFilePath(filePath);
        image.setFileName(request.getImage().getOriginalFilename());
        image.setFileSize(request.getImage().getSize());
        image.setContentType(request.getImage().getContentType());
        imageRepository.save(image);

        return mapToResponse(consultation);
    }

    @Transactional
    public ConsultationResponse confirmDiagnosis(Long consultationId, ConfirmDiagnosisRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User medico = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (!consultation.getMedico().getId().equals(medico.getId()) && medico.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Você não tem permissão para confirmar esta consulta");
        }

        consultation.setFinalDiagnosis(request.getFinalDiagnosis());
        consultation.setConfirmed(true);
        consultation = consultationRepository.save(consultation);

        return mapToResponse(consultation);
    }

    public List<ConsultationResponse> getMyConsultations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User medico = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        List<Consultation> consultations = consultationRepository.findByMedicoOrderByCreatedAtDesc(medico);

        return consultations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ConsultationResponse mapToResponse(Consultation consultation) {
        ConsultationResponse response = new ConsultationResponse();
        response.setId(consultation.getId());
        response.setPatient(mapToPatientResponse(consultation.getPatient()));
        response.setAiDiagnosis(consultation.getAiDiagnosis());
        response.setConfidence(consultation.getConfidence());
        response.setMultClass(consultation.getMultClass());
        response.setMultClassConfidence(consultation.getMultClassConfidence());
        response.setFinalDiagnosis(consultation.getFinalDiagnosis());
        response.setConfirmed(consultation.getConfirmed());
        response.setCreatedAt(consultation.getCreatedAt());
        return response;
    }

    private PatientResponse mapToPatientResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setNome(patient.getNome());
        response.setSexo(patient.getSexo().name());
        response.setDataNascimento(patient.getDataNascimento());
        return response;
    }
}

