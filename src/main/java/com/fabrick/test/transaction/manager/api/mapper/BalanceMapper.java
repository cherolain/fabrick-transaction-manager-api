package com.fabrick.test.transaction.manager.api.mapper;

import com.fabrick.test.transaction.manager.api.client.dto.response.balance.BalanceResponse;
import com.fabrick.test.transaction.manager.api.dto.balance.BalanceApiResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    BalanceApiResponse toBalanceApiResponse(BalanceResponse gbsBalanceResponse);
}
