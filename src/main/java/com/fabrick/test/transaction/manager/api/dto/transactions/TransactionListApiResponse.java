package com.fabrick.test.transaction.manager.api.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionListApiResponse {
    @Builder.Default
    private List<TransactionApiResponse> list = new ArrayList<>();
}