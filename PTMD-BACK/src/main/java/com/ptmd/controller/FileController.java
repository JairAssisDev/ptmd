package com.ptmd.controller;

import com.ptmd.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/**")
    public ResponseEntity<Resource> getFile(@RequestParam String path) {
        try {
            Path filePath = fileStorageService.loadFile(path);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                try {
                    String filename = filePath.getFileName().toString().toLowerCase();
                    if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                        contentType = "image/jpeg";
                    } else if (filename.endsWith(".png")) {
                        contentType = "image/png";
                    }
                } catch (Exception e) {
                    // Ignora erro de detecção de tipo
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-name/{filename:.+}")
    public ResponseEntity<Resource> getFileByName(@PathVariable String filename) {
        try {
            // Extrair apenas o nome do arquivo (sem diretórios)
            String actualFilename = filename;
            if (filename.contains("/")) {
                actualFilename = filename.substring(filename.lastIndexOf("/") + 1);
            }
            
            // Usar o FileStorageService para carregar o arquivo
            // O filePath salvo no banco pode ser o caminho completo ou apenas o nome
            // Tentamos primeiro com o caminho completo, depois apenas o nome
            Path filePath = null;
            Resource resource = null;
            
            // Tentar com o caminho completo primeiro (se filename contém caminho)
            if (filename.contains("/") || filename.contains("\\")) {
                try {
                    filePath = fileStorageService.loadFile(filename);
                    resource = new UrlResource(filePath.toUri());
                    if (!resource.exists() || !resource.isReadable()) {
                        resource = null;
                    }
                } catch (Exception e) {
                    // Continua para tentar apenas o nome
                }
            }
            
            // Se não encontrou, tentar apenas com o nome do arquivo
            if (resource == null) {
                // Tentar diferentes caminhos possíveis
                Path[] possiblePaths = {
                    Paths.get("uploads", actualFilename),
                    Paths.get("uploads/" + actualFilename),
                    Paths.get("/app/uploads/" + actualFilename),
                    Paths.get("/app/uploads", actualFilename)
                };
                
                for (Path path : possiblePaths) {
                    try {
                        Resource testResource = new UrlResource(path.toUri());
                        if (testResource.exists() && testResource.isReadable()) {
                            resource = testResource;
                            filePath = path;
                            break;
                        }
                    } catch (Exception e) {
                        // Continua tentando outros caminhos
                    }
                }
            }
            
            if (resource != null && resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                try {
                    String lowerFilename = actualFilename.toLowerCase();
                    if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                        contentType = "image/jpeg";
                    } else if (lowerFilename.endsWith(".png")) {
                        contentType = "image/png";
                    }
                } catch (Exception e) {
                    // Ignora erro de detecção de tipo
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + actualFilename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

