
package com.sprint.bank.admin.client;

import com.sprint.bank.admin.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class TransactionClient {

    private final RestClient restClient;

    public TransactionClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.transaction.url}") String transactionServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(transactionServiceUrl)
                .build();
    }

    public List<TransactionDto> getAllTransactions() {

        return restClient.get()
                .uri("/api/transactions")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TransactionDto>>() {});
    }
}
