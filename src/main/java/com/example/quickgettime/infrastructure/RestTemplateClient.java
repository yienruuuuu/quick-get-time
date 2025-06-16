package com.example.quickgettime.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * REST-TEMPLATE客戶端，用於製作HTTP請求。
 *
 * @author Eric.Lee
 * Date: 2025/5/22
 */
@Slf4j
@Component
public class RestTemplateClient {

    private final RestTemplate restTemplate;

    public RestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private <T> HttpEntity<T> createHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    public <T> T get(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(url, HttpMethod.GET, null, responseType, uriVariables);
    }

    public <T, R> T post(String url, R request, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(url, HttpMethod.POST, request, responseType, uriVariables);
    }

    public <T, R> T put(String url, R request, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(url, HttpMethod.PUT, request, responseType, uriVariables);
    }

    public <T> T delete(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(url, HttpMethod.DELETE, null, responseType, uriVariables);
    }

    public <T, R> T exchange(String url, HttpMethod method, R request, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        try {
            HttpEntity<R> requestEntity = createHttpEntity(request);
            log.debug("Sending {} request to {}", method, url);
            log.debug("Request body: {}", request);
            ResponseEntity<T> response = restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("REST call failed: {} {} => {}", method, url, e.getResponseBodyAsString(), e);
            throw e;
        }
    }
}

