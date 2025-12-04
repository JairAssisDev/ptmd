package com.ptmd.service;

import com.ptmd.dto.AiPredictionResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Predicate;

@Service
public class AiService {

    @Value("${app.ai-service.url}")
    private String aiServiceUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        if (aiServiceUrl == null || aiServiceUrl.isEmpty()) {
            throw new IllegalStateException("app.ai-service.url não está configurado");
        }
        
        this.webClient = WebClient.builder()
                .baseUrl(aiServiceUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB para imagens
                .build();
    }

    public AiPredictionResponse predict(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Imagem não pode ser nula ou vazia");
        }

        try {
            ByteArrayResource resource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename() != null ? image.getOriginalFilename() : "image.jpg";
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource)
                    .contentType(MediaType.parseMediaType(
                            image.getContentType() != null ? image.getContentType() : "image/jpeg"));

            Mono<AiPredictionResponse> responseMono = webClient.post()
                    .uri("/predict")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "Erro do serviço de IA (4xx): " + response.statusCode() + " - " + body)));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "Erro do serviço de IA (5xx): " + response.statusCode() + " - " + body)));
                    })
                    .bodyToMono(AiPredictionResponse.class)
                    .timeout(Duration.ofSeconds(60));

            return responseMono.block();
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            throw new RuntimeException("Erro ao comunicar com serviço de IA: " + e.getStatusCode() + 
                    (errorBody != null && !errorBody.isEmpty() ? " - " + errorBody : ""), e);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar imagem para IA: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao processar imagem para IA: " + e.getMessage(), e);
        }
    }
}
