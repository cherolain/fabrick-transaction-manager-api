package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import com.fabrick.test.transaction.manager.api.utils.GbsBankingErrorCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final GbsBankingClient gbsBankingClient;

    public Balance getAccountBalance(String accountId) {
        log.info("Requesting balance for accountId: {}", accountId);

        try {
            GbsBankingResponse<Balance> apiResponse = gbsBankingClient.retrieveAccountBalance(accountId);
            return handleGbsBankingResponse(accountId, apiResponse);
        } catch (GbsBankingBusinessException | GbsBankingApiException e) {
            log.error("GbsBanking API error during balance retrieval for account {}: {}", accountId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching balance for account {}: {}", accountId, e.getMessage(), e);
            throw new InternalApplicationException(
                    "An unexpected internal error occurred while processing account balance request.",
                    e,
                    ErrorCode.UNEXPECTED_ERROR
            );
        }
    }

    private Balance handleGbsBankingResponse(String accountId, GbsBankingResponse<Balance> response) {
        if (GbsBankingStatus.OK.equals(response.getStatus())) {
            log.info("Balance successfully retrieved for account ID: {}. Balance: {}", accountId, response.getPayload().getBalance());
            return response.getPayload();
        }

        // Usa GbsBankingStatus.KO per il confronto
        if (GbsBankingStatus.KO.equals(response.getStatus())) {
            log.error("GbsBanking API returned KO status with HTTP 200 for balance retrieval for account ID: {}. Errors: {}", accountId, response.getErrors());
            throw new GbsBankingBusinessException(response.getErrors(),
                    response.getErrors().stream()
                            .map(error -> GbsBankingErrorCodeMapper.resolveInternalErrorCode(error, HttpStatus.OK))
                            .toList());
        }

        // Questo caso non dovrebbe mai accadere se GbsBankingStatus copre tutti i casi.
        throw new InternalApplicationException("Unexpected status received from GbsBanking API", ErrorCode.UNEXPECTED_ERROR);
    }
}