package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.config.properties.KlineConfig;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.service.api.IStrategyManager;
import by.zemich.binancebot.service.api.ITraderBot;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final KlineConfig klineConfig;

    private final IStrategyManager strategyManager;

    private final Map<String, BarSeries> seriesMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService, KlineConfig klineConfig, IStrategyManager strategyManager) {
        this.stockMarketService = stockMarketService;
        this.klineConfig = klineConfig;
        this.strategyManager = strategyManager;
    }

    @Override
    public void updateSeries() {
        KlineQueryDto queryDto = new KlineQueryDto();
        String timeFrame = klineConfig.getTimeFrame();

        List<String> symbolsList = stockMarketService.getSpotSymbols().get();
        symbolsList.stream().forEach(symbol -> {
            queryDto.setSymbol(symbol);
            queryDto.setLimit(klineConfig.getLimit());
            queryDto.setInterval(klineConfig.getTimeFrame());
            seriesMap.put(symbol, stockMarketService.getBarSeries(queryDto).orElse(null));
        });


        stockMarketService.getBarSeries(queryDto);

    }

    @Override
    public void lookForEnterPosition() {
        if (!seriesMap.isEmpty()) {
            seriesMap.entrySet().stream().forEach(stringBarSeriesEntry -> {
                BarSeries series = stringBarSeriesEntry.getValue();
                Strategy strategy = strategyManager.get(series);
                if (strategy.shouldEnter(series.getEndIndex())) {
                    System.out.println("найдена точка входа");
                }
            });
        }
    }
}
