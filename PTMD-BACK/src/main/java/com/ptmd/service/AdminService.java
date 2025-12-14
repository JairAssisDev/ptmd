package com.ptmd.service;

import com.ptmd.dto.ChangePasswordRequest;
import com.ptmd.dto.DashboardResponse;
import com.ptmd.entity.User;
import com.ptmd.repository.ConsultationRepository;
import com.ptmd.repository.ImageRepository;
import com.ptmd.repository.PatientRepository;
import com.ptmd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public DashboardResponse getDashboard() {
        Long totalImages = imageRepository.count();
        Long totalConsultations = consultationRepository.count();
        Long totalPatients = patientRepository.count();

        return new DashboardResponse(totalImages, totalConsultations, totalPatients);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public byte[] generateBackup() throws IOException {
        // Buscar apenas imagens de consultas confirmadas pelo médico
        List<com.ptmd.entity.Image> confirmedImages = imageRepository.findConfirmedImagesWithDiagnosis();
        
        if (confirmedImages.isEmpty()) {
            throw new RuntimeException("Nenhuma imagem com diagnóstico confirmado pelo médico encontrada");
        }

        File tempZip = File.createTempFile("ptmd_database_", ".zip");
        File tempCsv = File.createTempFile("ptmd_database_", ".csv");
        
        try (FileOutputStream fos = new FileOutputStream(tempZip);
             ZipOutputStream zos = new ZipOutputStream(fos);
             PrintWriter csvWriter = new PrintWriter(new FileOutputStream(tempCsv))) {
            
            // Escrever cabeçalho do CSV
            csvWriter.println("Image ID,Patient ID,Model Prediction,Doctor Final Diagnosis");
            
            // Processar cada imagem confirmada
            for (com.ptmd.entity.Image image : confirmedImages) {
                // Verificar se o arquivo existe
                Path imagePath = Paths.get(image.getFilePath());
                if (!Files.exists(imagePath)) {
                    continue; // Pular se o arquivo não existir
                }

                // Obter informações da imagem, consulta e paciente
                String imageFileName = image.getFileName() != null ? image.getFileName() : "unknown";
                Long patientId = image.getConsultation().getPatient().getId();
                String aiDiagnosis = image.getAiDiagnosis();
                String finalDiagnosis = image.getFinalDiagnosis();
                
                // Escrever linha no CSV usando o nome da imagem
                csvWriter.printf("%s,%d,%s,%s%n", 
                    imageFileName, 
                    patientId, 
                    aiDiagnosis != null ? aiDiagnosis : "", 
                    finalDiagnosis != null ? finalDiagnosis : "");
                
                // Limpar o diagnóstico para usar como nome de arquivo (remover caracteres inválidos)
                String safeDiagnosis = sanitizeFileName(finalDiagnosis != null ? finalDiagnosis : "Unknown");
                
                // Obter extensão do arquivo original
                String originalFileName = image.getFileName();
                String extension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                } else {
                    // Tentar obter extensão do contentType
                    if (image.getContentType() != null) {
                        if (image.getContentType().contains("jpeg") || image.getContentType().contains("jpg")) {
                            extension = ".jpg";
                        } else if (image.getContentType().contains("png")) {
                            extension = ".png";
                        } else {
                            extension = ".jpg"; // Default
                        }
                    } else {
                        extension = ".jpg"; // Default
                    }
                }
                
                // Criar nome do arquivo: {imageId}_{patientId}_{finalDiagnosis}.{ext}
                String newFileName = String.format("%d_%d_%s%s", image.getId(), patientId, safeDiagnosis, extension);
                
                // Adicionar arquivo ao ZIP dentro da pasta "dataset"
                String zipEntryPath = "dataset/" + newFileName;
                addFileToZip(imagePath.toFile(), zipEntryPath, zos);
            }
            
            csvWriter.flush();
            
            // Adicionar CSV ao ZIP
            addFileToZip(tempCsv, "database.csv", zos);

            zos.finish();
        } finally {
            // Limpar arquivo temporário CSV
            if (tempCsv.exists()) {
                tempCsv.delete();
            }
        }

        byte[] zipBytes = Files.readAllBytes(tempZip.toPath());
        tempZip.delete();

        return zipBytes;
    }

    private String sanitizeFileName(String fileName) {
        // Remover caracteres inválidos para nome de arquivo
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void addFileToZip(File file, String zipEntryName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
        }
    }
}

