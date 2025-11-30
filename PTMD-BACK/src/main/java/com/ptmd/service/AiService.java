package com.ptmd.service;

import com.ptmd.dto.AiPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class AiService {

    @Value("${app.ai-service.url}")
    private String aiServiceUrl;

    private final WebClient webClient;

    public AiService() {
        this.webClient = WebClient.builder()
                .baseUrl(aiServiceUrl)
                .build();
    }

    public AiPredictionResponse predict(MultipartFile image) {
        try {
            ByteArrayResource resource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource)
                    .contentType(MediaType.parseMediaType(image.getContentType() != null ? image.getContentType() : "image/jpeg"));

            Mono<AiPredictionResponse> responseMono = webClient.post()
                    .uri("/predict")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(AiPredictionResponse.class);

            return responseMono.block();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar imagem para IA", e);
        }
    }
}

