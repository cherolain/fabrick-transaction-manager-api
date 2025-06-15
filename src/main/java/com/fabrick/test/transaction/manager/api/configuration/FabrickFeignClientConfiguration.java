package com.fabrick.test.transaction.manager.api.configuration;

import com.fabrick.test.transaction.manager.api.utils.FabrickErrorCodeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FabrickFeignClientConfiguration {

    @Bean
    public ErrorDecoder customFeignErrorDecoder(ObjectMapper objectMapper, FabrickErrorCodeMapper fabrickErrorCodeMapper) {
        return new FabrickFeignErrorDecoder(objectMapper, fabrickErrorCodeMapper);
    }

    @Bean
    public RequestInterceptor RequestHeaderInterceptor() {
        return new FabrickFeignHeaderInterceptor();
    }
}
