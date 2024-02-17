package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IAssetDao;
import by.zemich.binancebot.core.dto.ta4j.BarDto;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.service.api.AssetFacade;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class AssetFacadeImpl implements AssetFacade {
   private final IAssetDao assetDao;

    public AssetFacadeImpl(IAssetDao assetDao) {
        this.assetDao = assetDao;
    }

    @Override
    public Asset getBySymbol(String symbol) {
        return assetDao.getBySymbol(symbol);
    }

    @Override
    public List<Asset> getListToUSDTForSpotTrading() {
        return assetDao.getListToUSDTForSpotTrading();
    }

    @Override
    public BigDecimal getAskPrice(String assetSymbol) {
        return null;
    }

    @Override
    public BigDecimal getBidPrice(String assetSymbol) {
        return null;
    }

    @Override
    public BigDecimal getCurrentPrice(String assetSymbol) {
        return null;
    }

    @Override
    public List<BarDto> getBars(KlineQueryDto klineQuery) {
        return null;
    }

    @Override
    public BarSeries getBarSeries(KlineQueryDto klineQuery) {
        return null;
    }

    @Override
    public ExchangeInfoResponseDto getExchangeInfo(ExchangeInfoQueryDto queryDto) {
        return null;
    }

    @Override
    public List<SymbolShortDto> getAllSymbols(TickerSymbolShortQuery query) {
        return null;
    }

    @Override
    public List<String> getSpotSymbols() {
        return null;
    }

    @Override
    public List<Asset> getSymbols() {
        return null;
    }

    @Override
    public OrderBookTickerDto getOrderBookTicker(Map<String, Object> params) {
        return null;
    }

    @Override
    public SymbolPriceTickerDto getSymbolPriceTicker(Map<String, Object> params) {
        return null;
    }


}
