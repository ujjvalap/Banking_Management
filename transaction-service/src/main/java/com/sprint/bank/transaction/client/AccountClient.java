package com.sprint.bank.transaction.client;

import com.sprint.bank.transaction.dto.AccountDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class AccountClient {
    private final RestTemplate restTemplate;
    private final String accountServiceUrl;

    public AccountClient(RestTemplate restTemplate,
                         @Value("${account.service.url:http://localhost:8081}") String accountServiceUrl) {
        this.restTemplate = restTemplate;
        this.accountServiceUrl = accountServiceUrl;
    }

    public AccountDto getAccount(Long id) {
        return restTemplate.getForObject(accountServiceUrl + "/api/accounts/" + id, AccountDto.class);
    }

    public AccountDto deposit(Long id, BigDecimal amount) {
        Map<String, BigDecimal> body = Map.of("amount", amount);
        return restTemplate.postForObject(
                accountServiceUrl + "/api/accounts/" + id + "/deposit",
                body,
                AccountDto.class);
    }

    public AccountDto withdraw(Long id, BigDecimal amount) {
        Map<String, BigDecimal> body = Map.of("amount", amount);
        return restTemplate.postForObject(
                accountServiceUrl + "/api/accounts/" + id + "/withdraw",
                body,
                AccountDto.class);
    }

    public void rollbackWithdrawal(Long id, BigDecimal amount) {
        restTemplate.postForLocation(
                accountServiceUrl + "/api/accounts/" + id + "/deposit",
                Map.of("amount", amount));
    }
}
