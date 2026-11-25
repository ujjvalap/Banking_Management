
package com.sprint.bank.admin.client;

import com.sprint.bank.admin.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.auth.url}") String authServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }

    public List<CustomerDto> getAllCustomers() {

        return restClient.get()
                .uri("/api/auth/customers")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CustomerDto>>() {});
    }
}
