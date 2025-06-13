package com.fabrick.test.transaction.manager.api.client;

import com.fabrick.test.transaction.manager.api.configuration.FabrickFeignClientConfiguration;
import com.fabrick.test.transaction.manager.api.dto.BalanceDto;
import com.fabrick.test.transaction.manager.api.dto.FabrickApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "fabrick", configuration = FabrickFeignClientConfiguration.class)
public interface FabrickFeignClient {
    String BALANCE_ENDPOINT = "/api/gbs/banking/v4.0/accounts/{accountId}/balance";

    @RequestMapping(method = RequestMethod.GET, value = BALANCE_ENDPOINT)
    FabrickApiResponse<BalanceDto> getBalance(@PathVariable String accountId);

}