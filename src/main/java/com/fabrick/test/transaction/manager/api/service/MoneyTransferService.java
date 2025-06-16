package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import com.fabrick.test.transaction.manager.api.utils.GbsBankingErrorCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoneyTransferService {

    private final GbsBankingClient gbsBankingClient;

    public MoneyTransferResponse transferMoney(String accountId, MoneyTransferRequest moneyTransferRequest) {
        log.info("Initiating money transfer process for accountId: {}", accountId);

        try {
            GbsBankingResponse<MoneyTransferResponse> response = gbsBankingClient.createMoneyTransfer(
                    accountId,
                    moneyTransferRequest,
                    TimeZone.getDefault().getID()
            );
            return handleGbsBankingResponse(accountId, response);
        } catch (GbsBankingBusinessException | GbsBankingApiException e) {
            log.error("GbsBanking API error during money transfer for account {}: {}", accountId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during money transfer for account {}: {}", accountId, e.getMessage(), e);
            throw new InternalApplicationException("An unexpected internal error occurred while processing the money transfer.", e, ErrorCode.UNEXPECTED_ERROR);
        }
    }

    private MoneyTransferResponse handleGbsBankingResponse(String accountId, GbsBankingResponse<MoneyTransferResponse> response) {
        if (GbsBankingStatus.KO.equals(response.getStatus()) && response.getErrors() != null && !response.getErrors().isEmpty()) {
            if (isBp049Error(response)) {
                log.warn("Money transfer completed with GbsBanking business warning BP049 for accountId: {}. Response: {}", accountId, response.getPayload());
                return response.getPayload();
            }
        }

        if (GbsBankingStatus.OK.equals(response.getStatus())) {
            log.info("Money transfer successful for account ID: {}. Transfer ID: {}", accountId, response.getPayload().getMoneyTransferId());
            return response.getPayload();
        }

        log.error("GbsBanking API returned KO status with HTTP 200 for money transfer for account ID: {}. Errors: {}", accountId, response.getErrors());
        throw new GbsBankingBusinessException(response.getErrors(),
                response.getErrors().stream()
                        .map(error -> GbsBankingErrorCodeMapper.resolveInternalErrorCode(error, HttpStatus.OK))
                        .toList());
    }

    private boolean isBp049Error(GbsBankingResponse<MoneyTransferResponse> response) {
        return response.getErrors().stream().anyMatch(error -> "API000".equals(error.getCode()) && error.getDescription() != null && error.getDescription().contains("BP049"));
    }
}