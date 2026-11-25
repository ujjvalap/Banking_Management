
package com.sprint.bank.admin.client;

import com.sprint.bank.admin.dto.AccountDto;
import com.sprint.bank.admin.dto.StatusUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AccountClient {

    private final RestClient restClient;

    public AccountClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.account.url}") String accountServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(accountServiceUrl)
                .build();
    }

    public List<AccountDto> getAllAccounts() {

        return restClient.get()
                .uri("/api/accounts")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AccountDto>>() {});
    }

    public AccountDto updateAccountStatus(Long accountId, StatusUpdateRequest request) {

        return restClient.put()
                .uri("/api/accounts/{id}/status", accountId)
                .body(request)
                .retrieve()
                .body(AccountDto.class);
    }
}
