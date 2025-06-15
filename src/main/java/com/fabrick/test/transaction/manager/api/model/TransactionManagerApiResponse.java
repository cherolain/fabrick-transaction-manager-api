package com.fabrick.test.transaction.manager.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;


@Data
@AllArgsConstructor
public class TransactionManagerApiResponse<T> {

    private T payload;
    private List<TransactionManagerError> errors;

    @Data
    @AllArgsConstructor
    public static class TransactionManagerError {
        private String code;
        private String description;
    }
}
