package com.fabrick.test.transaction.manager.api.controller;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.model.TransactionManagerApiResponse;
import com.fabrick.test.transaction.manager.api.service.AccountApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

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
    public ResponseEntity<TransactionManagerApiResponse<Balance>> getBalance(
            @PathVariable
            @NotEmpty(message = "Account ID cannot be empty.")
            @Pattern(regexp = "^[0-9]+$", message = "Account ID must contain only digits.")
            String accountId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Balance response = accountApplicationService.getBalance(accountId);
        return buildSuccessResponse(response);
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
    public ResponseEntity<TransactionManagerApiResponse<MoneyTransferResponse>> transferMoney(
            @PathVariable
            @NotEmpty(message = "Account ID cannot be empty.")
            @Pattern(regexp = "^[0-9]+$", message = "Account ID must contain only digits.")
            String accountId,
            @Valid @RequestBody MoneyTransferRequest moneyTransferRequest,
            @RequestHeader("Authorization") String authHeader
    ) {
        MoneyTransferResponse response = accountApplicationService.transferMoney(accountId, moneyTransferRequest);
        return buildSuccessResponse(response);
    }


    /**
     * GET /api/v1/accounts/{accountId}/transactions
     * Retrieves a list of transactions for a specific account within a given date range.
     *
     * @param accountId     The ID of the account, passed as a path variable.
     * @param searchRequest The DTO containing the query parameters for the transaction search (from/to accounting date).
     * @param authHeader    The Authorization header.
     * @return A {@link ResponseEntity} containing the {@link TransactionListResponse} and an HTTP status code.
     */
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionManagerApiResponse<TransactionListResponse>> getTransactions(
            @PathVariable
            @NotEmpty(message = "Account ID cannot be empty.")
            @Pattern(regexp = "^[0-9]+$", message = "Account ID must contain only digits.")
            String accountId,
            @Valid TransactionSearchRequest searchRequest, // @Valid per attivare le validazioni nel DTO
            @RequestHeader("Authorization") String authHeader
    ) {
        TransactionListResponse transactions = accountApplicationService.getTransactions(accountId, searchRequest);
        return buildSuccessResponse(transactions);
    }

    private static <T> ResponseEntity<TransactionManagerApiResponse<T>> buildSuccessResponse(T body) {
        return ResponseEntity.ok(new TransactionManagerApiResponse<>(
                body,
                Collections.emptyList()
        ));
    }
}