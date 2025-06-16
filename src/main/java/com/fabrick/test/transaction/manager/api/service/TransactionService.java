package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import com.fabrick.test.transaction.manager.api.utils.GbsBankingErrorCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final GbsBankingClient gbsBankingClient;

    public TransactionListResponse getTransactions(String accountId, TransactionSearchRequest searchRequest) {
        log.info("Fetching transactions for accountId: {} from {} to {}", accountId, searchRequest.getFromAccountingDate(), searchRequest.getToAccountingDate());

        try {
            GbsBankingResponse<TransactionListResponse> response = gbsBankingClient.retrieveAccountTransactions(
                    accountId,
                    searchRequest
            );
            return handleGbsBankingTransactionResponse(accountId, response);
        } catch (GbsBankingBusinessException | GbsBankingApiException e) {
            log.error("GbsBanking API error during transaction fetch for account {}: {}", accountId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during transaction fetch for account {}: {}", accountId, e.getMessage(), e);
            throw new InternalApplicationException(
                    "An unexpected internal error occurred while fetching transactions.",
                    e,
                    ErrorCode.UNEXPECTED_ERROR
            );
        }
    }

    private TransactionListResponse handleGbsBankingTransactionResponse(String accountId, GbsBankingResponse<TransactionListResponse> response) {
        // Usa GbsBankingStatus.OK per il confronto
        if (GbsBankingStatus.OK.equals(response.getStatus())) {
            log.info("Transactions fetched successfully for account ID: {}.", accountId);
            return Optional.ofNullable(response.getPayload())
                    .orElseThrow(() -> new InternalApplicationException(
                            "GbsBanking API returned OK status but null transactions payload for account " + accountId,
                            ErrorCode.UNEXPECTED_ERROR
                    ));
        }

        // Usa GbsBankingStatus.KO per il confronto
        if (GbsBankingStatus.KO.equals(response.getStatus())) {
            log.error("GbsBanking API returned KO status with HTTP 200 for transaction fetch for account ID: {}. Errors: {}", accountId, response.getErrors());
            throw new GbsBankingBusinessException(response.getErrors(),
                    response.getErrors().stream()
                            .map(error -> GbsBankingErrorCodeMapper.resolveInternalErrorCode(error, HttpStatus.OK))
                            .toList());
        }

        // Questo caso non dovrebbe mai accadere se GbsBankingStatus copre tutti i casi.
        log.error("GbsBanking API returned an unhandled status for transaction fetch for account ID: {}. Status: {}", accountId, response.getStatus());
        throw new InternalApplicationException(
                "GbsBanking API returned an unhandled status for transaction fetch.",
                ErrorCode.UNEXPECTED_ERROR
        );
    }
}