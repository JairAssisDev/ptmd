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

        if (request.getImages() == null || request.getImages().isEmpty()) {
            throw new RuntimeException("Pelo menos uma imagem é obrigatória");
        }

        // Criar ou buscar paciente por CPF
        Patient patient = patientRepository.findByCpf(request.getPatient().getCpf())
                .orElse(null);
        
        if (patient == null) {
            patient = new Patient();
            patient.setNome(request.getPatient().getNome());
            patient.setCpf(request.getPatient().getCpf());
            patient.setSexo(request.getPatient().getSexo());
            patient.setDataNascimento(request.getPatient().getDataNascimento());
            patient = patientRepository.save(patient);
        } else {
            // Atualizar dados do paciente se necessário
            if (request.getPatient().getNome() != null && !request.getPatient().getNome().isEmpty()) {
                patient.setNome(request.getPatient().getNome());
            }
            if (request.getPatient().getDataNascimento() != null) {
                patient.setDataNascimento(request.getPatient().getDataNascimento());
            }
            patient = patientRepository.save(patient);
        }

        // Criar consulta
        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setMedico(medico);

        // Processar todas as imagens e analisar pela IA
        String firstAiDiagnosis = null;
        Double firstConfidence = null;
        String firstMultClass = null;
        Double firstMultClassConfidence = null;

        // Primeiro, salvar a consulta para obter o ID
        consultation = consultationRepository.save(consultation);

        for (MultipartFile imageFile : request.getImages()) {
            // Salvar imagem
            String filePath = fileStorageService.storeFile(imageFile);

            // Integração com IA para cada imagem
            AiPredictionResponse aiResponse = aiService.predict(imageFile);
            
            // Criar registro de imagem
            Image image = new Image();
            image.setConsultation(consultation);
            image.setFilePath(filePath);
            image.setFileName(imageFile.getOriginalFilename());
            image.setFileSize(imageFile.getSize());
            image.setContentType(imageFile.getContentType());
            
            // Salvar diagnóstico da IA para cada imagem
            if (aiResponse.getPredictions() != null && !aiResponse.getPredictions().isEmpty()) {
                AiPredictionResponse.Prediction prediction = aiResponse.getPredictions().get(0);
                image.setAiDiagnosis(prediction.getClassValue());
                image.setConfidence(prediction.getProbabilidade());
                
                if (prediction.getMultClass() != null && !prediction.getMultClass().isEmpty()) {
                    image.setMultClass(prediction.getMultClass());
                    image.setMultClassConfidence(prediction.getProbabilidadeMultClass());
                }
                
                // Usar o resultado da primeira imagem para o diagnóstico da consulta
                if (firstAiDiagnosis == null) {
                    firstAiDiagnosis = prediction.getClassValue();
                    firstConfidence = prediction.getProbabilidade();
                    if (prediction.getMultClass() != null && !prediction.getMultClass().isEmpty()) {
                        firstMultClass = prediction.getMultClass();
                        firstMultClassConfidence = prediction.getProbabilidadeMultClass();
                    }
                }
            }
            
            imageRepository.save(image);
        }

        // Definir diagnóstico da consulta baseado na primeira imagem
        if (firstAiDiagnosis != null) {
            consultation.setAiDiagnosis(firstAiDiagnosis);
            consultation.setConfidence(firstConfidence);
            if (firstMultClass != null) {
                consultation.setMultClass(firstMultClass);
                consultation.setMultClassConfidence(firstMultClassConfidence);
            }
            // Atualizar a consulta com o diagnóstico
            consultation = consultationRepository.save(consultation);
        }

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

        if (request.getFinalDiagnosis() == null) {
            throw new RuntimeException("Diagnóstico final é obrigatório");
        }

        consultation.setFinalDiagnosis(request.getFinalDiagnosis().getValue());
        consultation.setConfirmed(true);
        consultation = consultationRepository.save(consultation);

        // Recarregar a consulta com as imagens para retornar tudo
        Consultation consultationWithImages = consultationRepository.findByIdWithImages(consultation.getId());
        if (consultationWithImages == null) {
            consultationWithImages = consultation;
        }

        return mapToResponse(consultationWithImages);
    }

    public List<ConsultationResponse> getMyConsultations(String nome, String cpf) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User medico = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        List<Consultation> consultations;
        
        if ((nome != null && !nome.trim().isEmpty()) || (cpf != null && !cpf.trim().isEmpty())) {
            // Usar filtros
            consultations = consultationRepository.findByMedicoWithFilters(
                medico,
                nome != null && !nome.trim().isEmpty() ? nome.trim() : null,
                cpf != null && !cpf.trim().isEmpty() ? cpf.trim() : null
            );
        } else {
            // Sem filtros
            consultations = consultationRepository.findByMedicoOrderByCreatedAtDesc(medico);
        }

        return consultations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ImageResponse confirmImageDiagnosis(Long imageId, ConfirmImageDiagnosisRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User medico = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        Consultation consultation = image.getConsultation();
        if (!consultation.getMedico().getId().equals(medico.getId()) && medico.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Você não tem permissão para confirmar esta imagem");
        }

        if (request.getFinalDiagnosis() == null) {
            throw new RuntimeException("Diagnóstico final é obrigatório");
        }

        image.setFinalDiagnosis(request.getFinalDiagnosis().getValue());
        image.setConfirmed(true);
        image = imageRepository.save(image);

        return mapToImageResponse(image);
    }

    public ConsultationResponse getConsultationById(Long consultationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User medico = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Consultation consultation = consultationRepository.findByIdWithImages(consultationId);
        if (consultation == null) {
            throw new RuntimeException("Consulta não encontrada");
        }

        if (!consultation.getMedico().getId().equals(medico.getId()) && medico.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Você não tem permissão para acessar esta consulta");
        }

        return mapToResponse(consultation);
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
        
        // Mapear imagens
        if (consultation.getImages() != null) {
            List<ImageResponse> imageResponses = consultation.getImages().stream()
                    .map(this::mapToImageResponse)
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }
        
        return response;
    }

    private ImageResponse mapToImageResponse(Image image) {
        ImageResponse response = new ImageResponse();
        response.setId(image.getId());
        response.setFileName(image.getFileName());
        response.setFilePath(image.getFilePath());
        response.setFileSize(image.getFileSize());
        response.setContentType(image.getContentType());
        response.setAiDiagnosis(image.getAiDiagnosis());
        response.setConfidence(image.getConfidence());
        response.setMultClass(image.getMultClass());
        response.setMultClassConfidence(image.getMultClassConfidence());
        response.setFinalDiagnosis(image.getFinalDiagnosis());
        response.setConfirmed(image.getConfirmed());
        response.setCreatedAt(image.getCreatedAt());
        return response;
    }

    private PatientResponse mapToPatientResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setNome(patient.getNome());
        response.setCpf(patient.getCpf());
        response.setSexo(patient.getSexo().name());
        response.setDataNascimento(patient.getDataNascimento());
        return response;
    }
}

