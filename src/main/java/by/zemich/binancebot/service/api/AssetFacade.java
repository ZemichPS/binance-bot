package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.BarDto;
import by.zemich.binancebot.core.dto.binance.*;
import org.ta4j.core.BarSeries;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AssetFacade {
    Asset getBySymbol(String symbol);

    List<Asset> getListToUSDTForSpotTrading();

    BigDecimal getAskPrice(String assetSymbol);
    BigDecimal getBidPrice(String assetSymbol);
    BigDecimal getCurrentPrice(String assetSymbol);
    List<BarDto> getBars(KlineQueryDto klineQuery);
    BarSeries getBarSeries(KlineQueryDto klineQuery);
    ExchangeInfoResponseDto getExchangeInfo(ExchangeInfoQueryDto queryDto);
    List<SymbolShortDto> getAllSymbols(TickerSymbolShortQuery query);
    List<String> getSpotSymbols();
    List<Asset> getSymbols();
    OrderBookTickerDto getOrderBookTicker(Map<String, Object> params);
    SymbolPriceTickerDto getSymbolPriceTicker(Map<String, Object> params);
}
