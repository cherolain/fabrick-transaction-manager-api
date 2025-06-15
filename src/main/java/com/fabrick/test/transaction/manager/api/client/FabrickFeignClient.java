package com.fabrick.test.transaction.manager.api.client;

import com.fabrick.test.transaction.manager.api.configuration.FabrickFeignClientConfiguration;
import com.fabrick.test.transaction.manager.api.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.dto.response.moneytransfer.MoneyTransferResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "fabrick", configuration = FabrickFeignClientConfiguration.class)
public interface FabrickFeignClient {
    String BALANCE_ENDPOINT = "/api/gbs/banking/v4.0/accounts/{accountId}/balance";
    String MONEY_TRANSFER_ENDPOINT = "/api/gbs/banking/v4.0/accounts/{accountId}/payments/money-transfers";
    String MONEY_TRANSFER_VALIDATION_ENDPOINT = "/api/gbs/banking/v4.0/accounts/{accountId}/payments/money-transfers/validation";

    @RequestMapping(method = RequestMethod.GET, value = BALANCE_ENDPOINT)
    FabrickApiResponse<Balance> getBalance(@PathVariable String accountId);

    @RequestMapping(method = RequestMethod.POST, value = MONEY_TRANSFER_ENDPOINT)
    FabrickApiResponse<MoneyTransferResponse> createMoneyTransfer(
            @PathVariable String accountId,
            @RequestBody MoneyTransferRequest moneyTransferResponseRequest
    );

    @RequestMapping(method = RequestMethod.POST, value = MONEY_TRANSFER_VALIDATION_ENDPOINT)
    FabrickApiResponse<MoneyTransferResponse> validateMoneyTransfer(
            @PathVariable String accountId,
            @RequestBody MoneyTransferRequest moneyTransferRequest
    );
}