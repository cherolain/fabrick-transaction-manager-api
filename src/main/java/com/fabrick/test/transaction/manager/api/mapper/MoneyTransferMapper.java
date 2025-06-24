package com.fabrick.test.transaction.manager.api.mapper;

import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.*;
import com.fabrick.test.transaction.manager.api.dto.moneytransfer.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MoneyTransferMapper {

    MoneyTransferApiResponse toMoneyTransferApiResponse(MoneyTransferGbsResponse gbsResponse);

    CreditorApiResponse toCreditorApiResponse(CreditorResponse creditor);

    FeeApiResponse toFeeApiResponse(FeeResponse fee);

    CreditorAccountApiResponse toCreditorAccountApiResponse(CreditorAccountResponse creditorAccount);

    AmountApiResponse toAmountApiResponse(AmountResponse amountResponse);

}