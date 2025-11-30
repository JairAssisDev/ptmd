package com.ptmd.config;

import com.ptmd.entity.User;
import com.ptmd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.default.username:admin}")
    private String adminUsername;

    @Value("${app.admin.default.password:admin}")
    private String adminPassword;

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
        System.out.println("========================================");
        System.out.println("  Inicializando dados do sistema...");
        System.out.println("========================================");

        // Criar Administrador
        if (!userRepository.existsByEmail(adminUsername)) {
            User admin = new User();
            admin.setEmail(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setNome("Administrador Principal");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✓ Usuário ADMIN criado com sucesso!");
            System.out.println("  Email: " + adminUsername);
            System.out.println("  Senha: " + adminPassword);
        } else {
            System.out.println("ℹ Usuário ADMIN já existe: " + adminUsername);
        }

        // Criar Médico Mock
        if (!userRepository.existsByEmail(medicoEmail)) {
            User medico = new User();
            medico.setEmail(medicoEmail);
            medico.setPassword(passwordEncoder.encode(medicoPassword));
            medico.setNome(medicoNome);
            medico.setCpf(medicoCpf);
            medico.setCrm(medicoCrm);
            medico.setDataNascimento(LocalDate.of(1980, 5, 15));
            medico.setRole(User.Role.MEDICO);
            userRepository.save(medico);
            System.out.println("✓ Usuário MÉDICO criado com sucesso!");
            System.out.println("  Email: " + medicoEmail);
            System.out.println("  Senha: " + medicoPassword);
            System.out.println("  Nome: " + medicoNome);
            System.out.println("  CRM: " + medicoCrm);
        } else {
            System.out.println("ℹ Usuário MÉDICO já existe: " + medicoEmail);
        }

        System.out.println("========================================");
        System.out.println("  Inicialização concluída!");
        System.out.println("========================================");
    }
}

