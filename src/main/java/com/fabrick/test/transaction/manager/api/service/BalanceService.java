package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.FabrickFeignClient;
import com.fabrick.test.transaction.manager.api.dto.FabrickStatus;
import com.fabrick.test.transaction.manager.api.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiBusinessException;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final FabrickFeignClient fabrickClient;

    public Balance getAccountBalance(String accountId) {
        log.info("Requesting balance for accountId: {}", accountId);

        try {
            FabrickApiResponse<Balance> apiResponse = fabrickClient.getBalance(accountId);

            // Il FabrickFeignErrorDecoder gestisce già tutti gli status HTTP 4xx/5xx lanciando eccezioni.
            if (FabrickStatus.OK.equals(apiResponse.getStatus())) {
                log.debug("Balance successfully received for accountId: {}. Balance: {}", accountId, apiResponse.getPayload().getBalance());
                return apiResponse.getPayload();
            } else {
                log.warn("Fabrick API returned KO status for balance check with HTTP 200. AccountId: {}. Errors: {}", accountId, apiResponse.getErrors());
                throw new FabrickApiBusinessException(
                        "Fabrick API reported a business issue while fetching balance with OK HTTP status but KO internal status.",
                        apiResponse.getErrors(),
                        ErrorCode.EXTERNAL_API_FAILURE // Fallback
                );
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