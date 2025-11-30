package com.ptmd.service;

import com.ptmd.dto.JwtResponse;
import com.ptmd.dto.LoginRequest;
import com.ptmd.dto.RegisterRequest;
import com.ptmd.entity.User;
import com.ptmd.repository.UserRepository;
import com.ptmd.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("CPF já está em uso");
        }
        if (userRepository.existsByCrm(request.getCrm())) {
            throw new RuntimeException("CRM já está em uso");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNome(request.getNome());
        user.setCpf(request.getCpf());
        user.setCrm(request.getCrm());
        user.setDataNascimento(request.getDataNascimento());
        user.setRole(User.Role.MEDICO);

        return userRepository.save(user);
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return new JwtResponse(jwt, "Bearer", user.getEmail(), user.getRole().name());
    }
}

