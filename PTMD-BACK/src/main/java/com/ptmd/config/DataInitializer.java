package com.ptmd.config;

import com.ptmd.entity.User;
import com.ptmd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Admin Mock Config
    @Value("${app.admin.default.email:admin}")
    private String adminEmail;

    @Value("${app.admin.default.password:admin}")
    private String adminPassword;

    @Value("${app.admin.default.nome:Administrador}")
    private String adminNome;

    // Médico Mock Config
    @Value("${app.medico.default.email:medico@example.com}")
    private String medicoEmail;

    @Value("${app.medico.default.password:password}")
    private String medicoPassword;

    @Value("${app.medico.default.nome:Dr. Mock Teste}")
    private String medicoNome;

    @Value("${app.medico.default.cpf:123.456.789-00}")
    private String medicoCpf;

    @Value("${app.medico.default.crm:CRM/SP 123456}")
    private String medicoCrm;

    @Override
    public void run(String... args) {

        System.out.println("\n========================================");
        System.out.println("  Inicializando dados do sistema...");
        System.out.println("========================================");

        criarAdminMock();
        criarMedicoMock();

        System.out.println("========================================");
        System.out.println("  Inicialização concluída!");
        System.out.println("========================================\n");
    }

    private void criarAdminMock() {
        if (!userRepository.existsByEmail(adminEmail)) {

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setNome(adminNome);
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);

            System.out.println("✓ ADMIN criado com sucesso!");
            System.out.println("  Email: " + adminEmail);
            System.out.println("  Senha: " + adminPassword);

        } else {
            System.out.println("ℹ ADMIN já existe: " + adminEmail);
        }
    }

    private void criarMedicoMock() {
        if (!userRepository.existsByEmail(medicoEmail)) {

            User medico = new User();
            medico.setEmail(medicoEmail);
            medico.setPassword(passwordEncoder.encode(medicoPassword));
            medico.setNome(medicoNome);
            medico.setCpf(medicoCpf);
            medico.setCrm(medicoCrm);
            medico.setRole(User.Role.MEDICO);

            userRepository.save(medico);

            System.out.println("✓ MÉDICO criado com sucesso!");
            System.out.println("  Email: " + medicoEmail);
            System.out.println("  Senha: " + medicoPassword);

        } else {
            System.out.println("ℹ MÉDICO já existe: " + medicoEmail);
        }
    }
}
