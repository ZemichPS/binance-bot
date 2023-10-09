package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.AccountInformationQueryDto;
import by.zemich.binancebot.core.dto.AccountInformationResponseDto;
import by.zemich.binancebot.core.dto.AccountTradeQueryDto;
import by.zemich.binancebot.core.dto.AccountTradeResponseDto;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    Optional<AccountInformationResponseDto> getInformation(AccountInformationQueryDto accountInformation);
    Optional<List<AccountTradeResponseDto>> getTradeList(AccountTradeQueryDto tradeQuery);
}
