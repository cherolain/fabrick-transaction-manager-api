package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.FabrickFeignClient;
import com.fabrick.test.transaction.manager.api.dto.BalanceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import per il logging
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final FabrickFeignClient fabrickClient; // The service only depends on the client

    /**
     * Retrieves the balance for a given account ID by calling the Fabrick client.
     *
     * @param accountId The ID of the account whose balance is to be fetched.
     * @return A {@link BalanceDto} containing the account's balance information.
     * @throws // In a real app, we might throw a custom exception if the account is not found
     */
    public BalanceDto getAccountBalance(String accountId) {
        log.info("Requesting balance for accountId: {}", accountId);

        BalanceDto balance = fabrickClient.getBalance(accountId).getPayload();

        log.debug("Balance received for accountId: {}. Balance: {}", accountId, balance.getBalance());

        return balance;
    }
}

