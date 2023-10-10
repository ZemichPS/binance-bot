package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.BarDto;
import by.zemich.binancebot.core.dto.ExchangeInfoResponseDto;
import by.zemich.binancebot.core.dto.ExchangeInfoQueryDto;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import org.ta4j.core.BaseBar;

import java.util.List;
import java.util.Optional;

public interface IStockMarketService {
    Optional<List<BarDto>> getBars(KlineQueryDto klineQuery);
    Optional<List<BaseBar>> getBaseBars(KlineQueryDto klineQuery);
    Optional<ExchangeInfoResponseDto> getExchangeInfo(ExchangeInfoQueryDto queryDto);
}
