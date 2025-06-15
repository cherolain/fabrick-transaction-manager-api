package com.fabrick.test.transaction.manager.api.configuration;

import com.fabrick.test.transaction.manager.api.client.FabrickFeignClient;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
@Slf4j
public class FabrickFeignHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String url = template.url();
        if (url.contains(FabrickFeignClient.MONEY_TRANSFER_VALIDATION_ENDPOINT) ||
                url.contains(FabrickFeignClient.MONEY_TRANSFER_ENDPOINT)) {
            String currentTimeZone = TimeZone.getDefault().getID();
            template.header("X-Time-Zone", currentTimeZone);
            log.debug("Added X-Time-Zone header: {} to request for url: {}", currentTimeZone, url);
        }
    }
}