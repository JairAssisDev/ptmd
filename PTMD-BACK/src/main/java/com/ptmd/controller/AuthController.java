package com.ptmd.controller;

import com.ptmd.dto.JwtResponse;
import com.ptmd.dto.LoginRequest;
import com.ptmd.dto.RegisterRequest;
import com.ptmd.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de médicos")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Cadastrar médico", description = "Endpoint público para cadastro de novos médicos no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médico cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email/CPF/CRM já cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Médico cadastrado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Login", description = "Autenticação de médico ou admin. Retorna token JWT para uso nos demais endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Email ou senha inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou senha inválidos");
        }
    }
}

