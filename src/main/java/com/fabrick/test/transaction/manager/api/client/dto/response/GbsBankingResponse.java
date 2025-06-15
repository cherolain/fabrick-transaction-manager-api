package com.fabrick.test.transaction.manager.api.client.dto.response;

import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * A generic DTO to represent the standard envelope structure of GbsBanking API responses.
 * It uses Java Generics (<T>) to hold a payload of any type.
 *
 * @param <T> The type of the data contained within the payload.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class GbsBankingResponse<T> {

    private GbsBankingStatus status;
    private List<GbsBankingError> errors;
    private T payload;

    /**
     * Inner static class to represent an error object from GbsBanking.
     */
    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GbsBankingError {
        private String code;
        private String description;
    }
}