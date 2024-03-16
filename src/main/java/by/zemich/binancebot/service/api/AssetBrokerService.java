package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.ta4j.BarDto;
import by.zemich.binancebot.core.dto.binance.*;
import org.ta4j.core.BarSeries;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AssetBrokerService {
    Asset getBySymbol(String symbol);
    List<Asset> getAllSpotTradingToUsdtSpotTrading();
    List<Asset> getAllAvailableForSpotTrading();
    List<Asset> getAllAvailable();
    BigDecimal getAskPriceBySymbol(String assetSymbol);
    BigDecimal getBidPrice(String assetSymbol);
    BigDecimal getCurrentPrice(String assetSymbol);
    List<BarDto> getBars(KlineQueryDto klineQuery);
    BarSeries getBarSeries(KlineQueryDto klineQuery);
    ExchangeInfoResponseDto getExchangeInfo(ExchangeInfoQueryDto queryDto);
    OrderBookTickerDto getOrderBookTicker(Map<String, Object> params);
    SymbolPriceTickerDto getSymbolPriceTicker(String symbol);

}
