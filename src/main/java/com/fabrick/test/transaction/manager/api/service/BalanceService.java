package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.FabrickFeignClient;
import com.fabrick.test.transaction.manager.api.client.dto.FabrickStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiBusinessException;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import com.fabrick.test.transaction.manager.api.utils.FabrickErrorCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final FabrickFeignClient fabrickClient;

    public Balance getAccountBalance(String accountId) {
        log.info("Requesting balance for accountId: {}", accountId);

        try {
            FabrickApiResponse<Balance> apiResponse = fabrickClient.retrieveAccountBalance(accountId);

            // Il FabrickFeignErrorDecoder gestisce già tutti gli status HTTP 4xx/5xx lanciando eccezioni.
            if (FabrickStatus.OK.equals(apiResponse.getStatus())) {
                log.debug("Balance successfully received for accountId: {}. Balance: {}", accountId, apiResponse.getPayload().getBalance());
                return apiResponse.getPayload();
            } else {
                log.warn("Fabrick API returned KO status for balance check with HTTP 200. AccountId: {}. Errors: {}", accountId, apiResponse.getErrors());
                throw new FabrickApiBusinessException(apiResponse.getErrors(),
                        apiResponse.getErrors().stream()
                                .map(error -> FabrickErrorCodeMapper.resolveInternalErrorCode(error, HttpStatus.OK))
                                .toList());
            }
        } catch (FabrickApiBusinessException e) {
            log.warn("Fabrick API business validation error during balance retrieval for account {}: {}", accountId, e.getMessage(), e);
            throw e;
        } catch (FabrickApiException e) {
            log.error("Fabrick API HTTP error during balance retrieval for account {}: {}", accountId, e.getMessage());
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