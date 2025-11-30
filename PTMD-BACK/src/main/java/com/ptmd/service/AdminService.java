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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            throw new RuntimeException("Diretório de uploads não existe");
        }

        File tempZip = File.createTempFile("ptmd_backup_", ".zip");
        
        try (FileOutputStream fos = new FileOutputStream(tempZip);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File uploadDirFile = uploadPath.toFile();
            File[] files = uploadDirFile.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        addFileToZip(file, zos);
                    }
                }
            }

            zos.finish();
        }

        byte[] zipBytes = Files.readAllBytes(tempZip.toPath());
        tempZip.delete();

        return zipBytes;
    }

    private void addFileToZip(File file, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
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

