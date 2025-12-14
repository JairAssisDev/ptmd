package com.ptmd.controller;

import com.ptmd.dto.ConfirmDiagnosisRequest;
import com.ptmd.dto.ConfirmImageDiagnosisRequest;
import com.ptmd.dto.ConsultationRequest;
import com.ptmd.dto.ConsultationResponse;
import com.ptmd.dto.ImageResponse;
import com.ptmd.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/medico/consultations")
@CrossOrigin(origins = "*")
@Tag(name = "Consultas Médicas", description = "Endpoints para gerenciamento de consultas médicas")
@SecurityRequirement(name = "bearerAuth")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Operation(summary = "Criar consulta", description = "Cria uma nova consulta com paciente e múltiplas imagens. " +
            "Todas as imagens são enviadas para o microsserviço Python de IA que retorna um diagnóstico preliminar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar imagem ou integrar com IA")
    })
    @PostMapping
    public ResponseEntity<?> createConsultation(@Valid @ModelAttribute ConsultationRequest request) {
        try {
            ConsultationResponse response = consultationService.createConsultation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar imagem: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Confirmar diagnóstico", description = "Confirma o diagnóstico da IA ou permite que o médico escolha outro diagnóstico manualmente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnóstico confirmado",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Consulta não encontrada ou sem permissão")
    })
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmDiagnosis(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmDiagnosisRequest request) {
        try {
            ConsultationResponse response = consultationService.confirmDiagnosis(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar minhas consultas", description = "Retorna todas as consultas do médico logado, ordenadas por data (mais recente primeiro). " +
            "Suporta filtros opcionais por nome e CPF do paciente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<ConsultationResponse>> getMyConsultations(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf) {
        List<ConsultationResponse> consultations = consultationService.getMyConsultations(nome, cpf);
        return ResponseEntity.ok(consultations);
    }

    @Operation(summary = "Obter consulta por ID", description = "Retorna os detalhes completos de uma consulta, incluindo todas as imagens e seus diagnósticos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Consulta não encontrada ou sem permissão")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getConsultationById(@PathVariable Long id) {
        try {
            ConsultationResponse response = consultationService.getConsultationById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Confirmar diagnóstico de imagem", description = "Confirma o diagnóstico de uma imagem específica dentro de uma consulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnóstico confirmado",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Imagem não encontrada ou sem permissão")
    })
    @PutMapping("/images/{imageId}/confirm")
    public ResponseEntity<?> confirmImageDiagnosis(
            @PathVariable Long imageId,
            @Valid @RequestBody ConfirmImageDiagnosisRequest request) {
        try {
            ImageResponse response = consultationService.confirmImageDiagnosis(imageId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

