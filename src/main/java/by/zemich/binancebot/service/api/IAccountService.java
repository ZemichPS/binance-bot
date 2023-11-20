package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.binance.AccountInformationQueryDto;
import by.zemich.binancebot.core.dto.binance.AccountInformationResponseDto;
import by.zemich.binancebot.core.dto.binance.AccountTradeQueryDto;
import by.zemich.binancebot.core.dto.binance.AccountTradeResponseDto;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    Optional<AccountInformationResponseDto> getInformation(AccountInformationQueryDto accountInformation);
    Optional<List<AccountTradeResponseDto>> getTradeList(AccountTradeQueryDto tradeQuery);
}
