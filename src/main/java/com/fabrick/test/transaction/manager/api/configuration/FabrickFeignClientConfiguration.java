package com.fabrick.test.transaction.manager.api.configuration;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FabrickFeignClientConfiguration {

    @Bean
    public ErrorDecoder customFeignErrorDecoder() {
        return new FabrickFeignErrorDecoder();
    }
}
