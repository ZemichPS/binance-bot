package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.config.properties.KlineConfig;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.service.api.ITraderBot;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final KlineConfig klineConfig;

private final Map<String, BarSeries> seriesMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService, KlineConfig klineConfig) {
        this.stockMarketService = stockMarketService;
        this.klineConfig = klineConfig;
    }

    @Override
    public void updateSeries() {
        KlineQueryDto queryDto = new KlineQueryDto();
        String timeFrame = klineConfig.getTimeFrame();

        List<String> symbolsList = stockMarketService.getSpotSymbols().get();


        stockMarketService.getBarSeries(queryDto);

    }

    @Override
    public void lookForPosition() {

    }
}
