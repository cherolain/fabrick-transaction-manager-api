package com.fabrick.test.transaction.manager.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FabrickFeignClientConfiguration {

    @Bean
    public ErrorDecoder customFeignErrorDecoder(ObjectMapper objectMapper) {
        return new FabrickFeignErrorDecoder(objectMapper);
    }
}
