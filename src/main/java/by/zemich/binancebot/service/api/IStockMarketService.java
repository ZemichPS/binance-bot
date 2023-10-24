package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.*;
import org.ta4j.core.BarSeries;

import java.util.List;
import java.util.Optional;

public interface IStockMarketService {
    Optional<List<BarDto>> getBars(KlineQueryDto klineQuery);
    Optional<BarSeries> getBarSeries(KlineQueryDto klineQuery);
    Optional<ExchangeInfoResponseDto> getExchangeInfo(ExchangeInfoQueryDto queryDto);
    Optional<List<SymbolShortDto>> getAllSymbols(TickerSymbolShortQuery query);
    Optional<List<String>> getSpotSymbols();
}
