package com.ptmd.controller;

import com.ptmd.dto.ChangePasswordRequest;
import com.ptmd.dto.DashboardResponse;
import com.ptmd.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Administração", description = "Endpoints administrativos (requer role ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Dashboard", description = "Retorna estatísticas do sistema: total de imagens, consultas e pacientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class)))
    })
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse dashboard = adminService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @Operation(summary = "Alterar senha", description = "Permite ao administrador alterar sua própria senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta")
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            adminService.changePassword(request);
            return ResponseEntity.ok("Senha alterada com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Download database", description = "Gera e retorna um arquivo ZIP contendo imagens de consultas confirmadas, renomeadas com ID da imagem, ID do paciente e diagnóstico da IA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo ZIP gerado com sucesso",
                    content = @Content(mediaType = "application/zip")),
            @ApiResponse(responseCode = "400", description = "Nenhuma imagem com diagnóstico confirmado encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro ao gerar backup")
    })
    @GetMapping("/backup")
    public ResponseEntity<byte[]> downloadBackup() {
        try {
            byte[] zipBytes = adminService.generateBackup();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "ptmd_database.zip");
            headers.setContentLength(zipBytes.length);

            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .header("X-Error-Message", e.getMessage())
                    .build();
        }
    }
}

