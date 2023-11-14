package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.*;
import org.ta4j.core.BarSeries;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IStockMarketService {
    Optional<List<BarDto>> getBars(KlineQueryDto klineQuery);
    Optional<QueryOrderResponseDto> getOrder(QueryOrderDto queryOrder);
    Optional<BarSeries> getBarSeries(KlineQueryDto klineQuery);
    Optional<ExchangeInfoResponseDto> getExchangeInfo(ExchangeInfoQueryDto queryDto);
    Optional<List<SymbolShortDto>> getAllSymbols(TickerSymbolShortQuery query);
    Optional<List<String>> getSpotSymbols();
    Optional<NewOrderFullResponseDto> createOrder(Map<String, Object> params);
    Optional<List<HistoricalOrderResponseDto>> getHistoricalOrderList(Map<String, Object> params);
    Optional<CancelOrderResponseDto> cancelOrder(Map<String, Object> params);
    Optional<OrderBookTickerDto> getOrderBookTicker(Map<String, Object> params);
    Optional<SymbolPriceTickerDto> getSymbolPriceTicker(Map<String, Object> params);
    Optional<AccountInformationResponseDto> getAccountInformation(Map<String, Object> params);


}
