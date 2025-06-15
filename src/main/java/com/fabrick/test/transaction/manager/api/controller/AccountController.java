package com.fabrick.test.transaction.manager.api.controller;

import com.fabrick.test.transaction.manager.api.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.service.AccountApplicationService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts") // Base path for all endpoints in this controller
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountApplicationService accountApplicationService;

    /**
     * GET /api/v1/accounts/{accountId}/balance
     * Retrieves the balance for a specific account.
     *
     * @param accountId The ID of the account, passed as a path variable.
     * @return A {@link ResponseEntity} containing the {@link Balance} and an HTTP status code.
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Balance> getBalance(
            @PathVariable
            @NotEmpty(message = "Account ID cannot be empty.")
            @Pattern(regexp = "^[0-9]+$", message = "Account ID must contain only digits.")
            String accountId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Balance balance = accountApplicationService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    /**
     * POST /api/v1/accounts/{accountId}/money-transfer
     * Initiates a money transfer for a specific account.
     *
     * @param accountId            The ID of the account, passed as a path variable.
     * @param moneyTransferRequest The request body containing the details of the money transfer.
     * @return A {@link ResponseEntity} containing the {@link MoneyTransferResponse} and an HTTP status code.
     */

    @PostMapping("/{accountId}/money-transfer")
    public ResponseEntity<MoneyTransferResponse> transferMoney(
            @PathVariable
            @NotEmpty(message = "Account ID cannot be empty.")
            @Pattern(regexp = "^[0-9]+$", message = "Account ID must contain only digits.")
            String accountId,
            @RequestBody MoneyTransferRequest moneyTransferRequest,
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("X-Time-Zone") String timeZone
    ) {
        MoneyTransferResponse response = accountApplicationService.transferMoney(accountId, moneyTransferRequest);
        return ResponseEntity.ok(response);
    }

}