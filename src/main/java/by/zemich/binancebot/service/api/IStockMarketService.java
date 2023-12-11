package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.EOrderStatus;
import org.ta4j.core.BarSeries;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IStockMarketService {
    OrderDto createOrder(RequestForNewOrderDto requestForNewOrderDto);
    BigDecimal getAskPriceForAsset(String assetSymbol);

    BigDecimal getBidPriceForAsset(String assetSymbol);
    BigDecimal getCurrentPriceForAsset(String assetSymbol);

    EOrderStatus getOrderStatus(QueryOrderDto queryOrder);

    CancelOrderResponseDto cancelOrder (CancelOrderRequestDto cancelOrderRequestDto);
    Optional<List<BarDto>> getBars(KlineQueryDto klineQuery);
    Optional<BarSeries> getBarSeries(KlineQueryDto klineQuery);
    Optional<ExchangeInfoResponseDto> getExchangeInfo(ExchangeInfoQueryDto queryDto);
    Optional<List<SymbolShortDto>> getAllSymbols(TickerSymbolShortQuery query);
    Optional<List<String>> getSpotSymbols();
    Optional<List<Asset>> getSymbols();
    Optional<List<HistoricalOrderResponseDto>> getHistoricalOrderList(Map<String, Object> params);
    Optional<OrderBookTickerDto> getOrderBookTicker(Map<String, Object> params);
    Optional<SymbolPriceTickerDto> getSymbolPriceTicker(Map<String, Object> params);
    Optional<AccountInformationResponseDto> getAccountInformation(Map<String, Object> params);


}
