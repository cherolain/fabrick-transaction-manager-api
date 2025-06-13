package com.fabrick.test.transaction.manager.api.controller;

import com.fabrick.test.transaction.manager.api.dto.BalanceDto;
import com.fabrick.test.transaction.manager.api.service.AccountService;
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

    private final AccountService accountService;

    /**
     * GET /api/v1/accounts/{accountId}/balance
     *
     * Retrieves the balance for a specific account.
     *
     * @param accountId The ID of the account, passed as a path variable.
     * @return A {@link ResponseEntity} containing the {@link BalanceDto} and an HTTP status code.
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceDto> getBalance(
            @PathVariable
            @NotEmpty(message = "Account ID cannot be empty.")
            @Pattern(regexp = "^[0-9]+$", message = "Account ID must contain only digits.")
            String accountId,
            @RequestHeader("Authorization") String authHeader
    ) {
        BalanceDto balanceDto = accountService.getAccountBalance(accountId);

        return ResponseEntity.ok(balanceDto);
    }
}