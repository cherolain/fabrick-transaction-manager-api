package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.FabrickFeignClient;
import com.fabrick.test.transaction.manager.api.client.dto.FabrickStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiBusinessException;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import com.fabrick.test.transaction.manager.api.utils.FabrickErrorCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final FabrickFeignClient fabrickClient;

    public TransactionListResponse getTransactions(String accountId, TransactionSearchRequest searchRequest) {
        log.info("Fetching transactions for accountId: {} from {} to {}", accountId, searchRequest.getFromAccountingDate(), searchRequest.getToAccountingDate());

        try {
            FabrickApiResponse<TransactionListResponse> response = fabrickClient.retrieveAccountTransactions(
                    accountId,
                    searchRequest
            );
            return handleFabrickTransactionResponse(accountId, response);
        } catch (FabrickApiBusinessException | FabrickApiException e) {
            log.error("Fabrick API error during transaction fetch for account {}: {}", accountId, e.getMessage(), e);
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

    private TransactionListResponse handleFabrickTransactionResponse(String accountId, FabrickApiResponse<TransactionListResponse> response) {
        // Usa FabrickStatus.OK per il confronto
        if (FabrickStatus.OK.equals(response.getStatus())) {
            log.info("Transactions fetched successfully for account ID: {}.", accountId);
            return Optional.ofNullable(response.getPayload())
                    .orElseThrow(() -> new InternalApplicationException(
                            "Fabrick API returned OK status but null transactions payload for account " + accountId,
                            ErrorCode.UNEXPECTED_ERROR
                    ));
        }

        // Usa FabrickStatus.KO per il confronto
        if (FabrickStatus.KO.equals(response.getStatus())) {
            log.error("Fabrick API returned KO status with HTTP 200 for transaction fetch for account ID: {}. Errors: {}", accountId, response.getErrors());
            throw new FabrickApiBusinessException(response.getErrors(),
                    response.getErrors().stream()
                            .map(error -> FabrickErrorCodeMapper.resolveInternalErrorCode(error, HttpStatus.OK))
                            .toList());
        }

        // Questo caso non dovrebbe mai accadere se FabrickStatus copre tutti i casi.
        log.error("Fabrick API returned an unhandled status for transaction fetch for account ID: {}. Status: {}", accountId, response.getStatus());
        throw new InternalApplicationException(
                "Fabrick API returned an unhandled status for transaction fetch.",
                ErrorCode.UNEXPECTED_ERROR
        );
    }
}