package com.example.consumingrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ConsumingRestApplication {

    private static final Logger log = LoggerFactory.getLogger(ConsumingRestApplication.class);
    private static final String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) {
        SpringApplication.run(ConsumingRestApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
            log.info("=== Starting API consumption tests ===");

            // Test 1: GET /apiWithHeader with requestId header
            testGetWithHeader(restTemplate);

            // Test 2: GET /apiWithRequestParam?id=11
            testGetWithRequestParam(restTemplate);

            // Test 3: POST /api/quote
            testPostQuote(restTemplate);

            // Test 4: DELETE /api/quote/12
            testDeleteQuote(restTemplate);

            log.info("=== API consumption tests completed ===");
        };
    }

    private void testGetWithHeader(RestTemplate restTemplate) {
        try {
            log.info("--- Testing GET /apiWithHeader with requestId header ---");

            HttpHeaders headers = new HttpHeaders();
            headers.set("requestId", "someID");
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Quote[]> response = restTemplate.exchange(
                    BASE_URL + "/apiWithHeader",
                    HttpMethod.GET,
                    entity,
                    Quote[].class
            );

            Quote[] quotes = response.getBody();
            log.info("Response status: {}", response.getStatusCode());
            log.info("Number of quotes received: {}", quotes != null ? quotes.length : 0);

            if (quotes != null && quotes.length > 0) {
                for (int i = 0; i < Math.min(3, quotes.length); i++) {
                    Quote quote = quotes[i];
                    log.info("Quote {}: type='{}', id={}, text='{}'",
                            i + 1,
                            quote.type(),
                            quote.value() != null ? quote.value().id() : null,
                            quote.value() != null ? quote.value().quote() : null);
                }
            }

        } catch (Exception e) {
            log.error("Error testing GET /apiWithHeader: {}", e.getMessage());
        }
    }

    private void testGetWithRequestParam(RestTemplate restTemplate) {
        try {
            log.info("--- Testing GET /apiWithRequestParam?id=11 ---");

            Quote quote = restTemplate.getForObject(
                    BASE_URL + "/apiWithRequestParam?id=11",
                    Quote.class
            );

            log.info("Quote retrieved: {}", quote);
            if (quote != null) {
                log.info("Quote type: {}", quote.type());
                if (quote.value() != null) {
                    log.info("Quote ID: {}", quote.value().id());
                    log.info("Quote text: '{}'", quote.value().quote());
                }
            }

        } catch (Exception e) {
            log.error("Error testing GET /apiWithRequestParam: {}", e.getMessage());
        }
    }

    private void testPostQuote(RestTemplate restTemplate) {
        try {
            log.info("--- Testing POST /api/quote ---");

            // Create a Value record and Quote record for POST request
            Value newValue = new Value(null, "some quote");
            Quote newQuote = new Quote("success", newValue);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Quote> entity = new HttpEntity<>(newQuote, headers);

            ResponseEntity<Quote> response = restTemplate.exchange(
                    BASE_URL + "/api/quote",
                    HttpMethod.POST,
                    entity,
                    Quote.class
            );

            log.info("Response status: {}", response.getStatusCode());
            Quote createdQuote = response.getBody();
            log.info("Created quote: {}", createdQuote);

            if (createdQuote != null) {
                log.info("Created quote type: {}", createdQuote.type());
                if (createdQuote.value() != null) {
                    log.info("New quote ID: {}", createdQuote.value().id());
                    log.info("New quote text: '{}'", createdQuote.value().quote());
                }
            }

        } catch (Exception e) {
            log.error("Error testing POST /api/quote: {}", e.getMessage());
        }
    }

    private void testDeleteQuote(RestTemplate restTemplate) {
        try {
            log.info("--- Testing DELETE /api/quote/12 ---");

            ResponseEntity<Quote> response = restTemplate.exchange(
                    BASE_URL + "/api/quote/12",
                    HttpMethod.DELETE,
                    null,
                    Quote.class
            );

            log.info("Response status: {}", response.getStatusCode());
            Quote result = response.getBody();
            log.info("Delete result: {}", result);

            if (result != null) {
                log.info("Delete response type: {}", result.type());
                if (result.value() != null) {
                    log.info("Delete response quote: '{}'", result.value().quote());
                }
            }

        } catch (Exception e) {
            log.error("Error testing DELETE /api/quote/12: {}", e.getMessage());
        }
    }
}