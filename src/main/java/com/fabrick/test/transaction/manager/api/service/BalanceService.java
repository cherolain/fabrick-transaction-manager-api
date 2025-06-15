package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import com.fabrick.test.transaction.manager.api.utils.GbsBankingPaymentsErrorCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final GbsBankingClient fabrickClient;

    public Balance getAccountBalance(String accountId) {
        log.info("Requesting balance for accountId: {}", accountId);

        try {
            GbsBankingResponse<Balance> apiResponse = fabrickClient.retrieveAccountBalance(accountId);

            // Il GbsBankingFeignErrorDecoder gestisce già tutti gli status HTTP 4xx/5xx lanciando eccezioni.
            if (GbsBankingStatus.OK.equals(apiResponse.getStatus())) {
                log.debug("Balance successfully received for accountId: {}. Balance: {}", accountId, apiResponse.getPayload().getBalance());
                return apiResponse.getPayload();
            } else {
                log.warn("GbsBanking API returned KO status for balance check with HTTP 200. AccountId: {}. Errors: {}", accountId, apiResponse.getErrors());
                throw new GbsBankingBusinessException(apiResponse.getErrors(),
                        apiResponse.getErrors().stream()
                                .map(error -> GbsBankingPaymentsErrorCodeMapper.resolveInternalErrorCode(error, HttpStatus.OK))
                                .toList());
            }
        } catch (GbsBankingBusinessException e) {
            log.warn("GbsBanking API business validation error during balance retrieval for account {}: {}", accountId, e.getMessage(), e);
            throw e;
        } catch (GbsBankingApiException e) {
            log.error("GbsBanking API HTTP error during balance retrieval for account {}: {}", accountId, e.getMessage());
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
}