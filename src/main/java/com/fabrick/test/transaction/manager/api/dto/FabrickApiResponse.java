package com.fabrick.test.transaction.manager.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * A generic DTO to represent the standard envelope structure of Fabrick API responses.
 * It uses Java Generics (<T>) to hold a payload of any type.
 *
 * @param <T> The type of the data contained within the payload.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FabrickApiResponse<T> {

    private FabrickStatus status;
    private List<FabrickError> error;
    private T payload;

    /**
     * Inner static class to represent an error object from Fabrick.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FabrickError {
        private String code;
        private String description;
    }
}